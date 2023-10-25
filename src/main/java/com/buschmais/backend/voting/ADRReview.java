package com.buschmais.backend.voting;

import com.buschmais.backend.utils.Opt;
import com.buschmais.backend.config.RecursiveSaving;
import com.buschmais.backend.notifications.VotingPendingNotification;
import com.buschmais.backend.users.User;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class ADRReview {
	@RecursiveSaving
	@Setter(AccessLevel.PRIVATE)
	@DBRef
	private Set<User> invitedVoters;

	@Setter(AccessLevel.PRIVATE)
	@Getter(AccessLevel.PRIVATE)
	private Map<String, @NonNull Opt<VoteType>> votes;

	public ADRReview(){
		this.invitedVoters = new HashSet<>();
		this.votes = new HashMap<>();
	}

	/**
	 * get vote results
	 * @return Map with corresponding count for each VoteType. Keys:
	 * <lu><li>VoteType.FOR</li> <li>VoteType.AGAINST</li> <li>VoteType.OTHER</li></lu>
	 */
	public Map<VoteType, Integer> getVoteResult(){
		synchronize();

		HashMap<VoteType, Integer> res = new HashMap<>();
		res.put(VoteType.FOR, 0);
		res.put(VoteType.AGAINST, 0);

		for (Opt<VoteType> v : this.votes.values()){
			v.ifPresent(vote -> res.put(vote, res.get(vote) + 1));
		}
		return res;
	}

	public boolean addVoter(@NonNull final User user){
		synchronize();

		if(this.invitedVoters.contains(user)) return false;

		this.invitedVoters.add(user);
		return this.votes.put(user.getId(), Opt.Empty()) != null;
	}

	/**
	 * removes a user from the invitedVoters if he did not already vote
	 * @param user a user to be removed
	 * @return <li>-1, if user isn't invited</li> <li>0, if user already voted</li> <li>1, if user was successfully removed</li>
	 */
	public byte removeVoter(@NonNull final User user) {
		synchronize();

		if(this.userHasVoted(user).isEmpty()) return -1;

		if(this.userHasVoted(user).get()) {
			return 0;
		}

		this.invitedVoters.remove(user);
		this.votes.remove(user.getId());
		return 1;

	}

	/**
	 *
	 * @param user the user that should be checked
	 * @return empty, if user isn't invited; true, if user has voted; false if user has not voted
	 */
	public Optional<Boolean> userHasVoted(@NonNull final User user){
		synchronize();

		if(!this.invitedVoters.contains(user)) return Optional.empty();
		return Optional.of(this.votes.get(user.getId()).isPresent());
	}

	/**
	 * @param user the user that should be checked
	 * @return empty, if user is not invited or if user has not voted; corresponding VoteType, if user has voted
	 */
	public Optional<VoteType> getUserVote(@NonNull final User user){
		synchronize();

		if(!this.invitedVoters.contains(user)) return Optional.empty();
		return this.votes.get(user.getId()).stdOpt();
	}

	public void putVote(@NonNull final User user, final VoteType vote) throws UserIsNotInvitedException {
		synchronize();

		if(!this.invitedVoters.contains(user)) throw new UserIsNotInvitedException();

		this.votes.put(user.getId(), Opt.of(vote));
	}

	// Remove Vote
	public void removeVote(@NonNull final User user) throws UserIsNotInvitedException {
		synchronize();

		if(!this.invitedVoters.contains(user)) throw new UserIsNotInvitedException();

		this.votes.put(user.getId(), Opt.Empty());
	}

	private void synchronize() {
		if(this.votes.size() < this.invitedVoters.size()) {
			for(User u : this.invitedVoters) {
				if(!this.votes.containsKey(u.getId())) {
					this.votes.put(u.getId(), Opt.Empty());
				}
			}
		}
		else if(this.votes.keySet().size() > this.invitedVoters.size()) {
			List<String> userIdsToRemove = new LinkedList<>();
			for(String uId : this.votes.keySet()) {
				userIdsToRemove.add(uId);
				for(User u : this.invitedVoters) {
					if(u.getId().equals(uId)) {
						userIdsToRemove.remove(uId);
						break;
					}
				}
			}
			for(String id : userIdsToRemove) {
				this.votes.remove(id);
			}
		}
	}
}
