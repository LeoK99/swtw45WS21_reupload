package com.buschmais.backend.adr;

import com.buschmais.backend.adr.dataAccess.ADRDao;
import com.buschmais.backend.adr.external.ExternalContent;
import com.buschmais.backend.adr.status.*;
import com.buschmais.backend.adrAccess.AccessGroup;
import com.buschmais.backend.adrAccess.AccessRights;
import com.buschmais.backend.config.RecursiveSaving;
import com.buschmais.backend.users.User;
import com.buschmais.backend.users.UserRights;
import com.buschmais.backend.voting.ADRReview;
import com.buschmais.backend.voting.VoteType;
import lombok.*;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.*;

@Document
@Data
public final class ADR {

	@Setter(AccessLevel.PRIVATE)
	@EqualsAndHashCode.Exclude
	@Id
	private String id;

	@lombok.NonNull
	private Instant timeStamp;

	@Setter(AccessLevel.PRIVATE)
	@RecursiveSaving
	@DBRef(lazy = true)
	private ADRContainer parent;

	@lombok.NonNull
	@Indexed
	private String name;

	@Getter(AccessLevel.NONE)
	@EqualsAndHashCode.Exclude
	@RecursiveSaving
	@Indexed
	@DBRef
	private User author;

	@EqualsAndHashCode.Exclude
	@lombok.NonNull
	@Indexed
	private String title;

	@EqualsAndHashCode.Exclude
	@lombok.NonNull
	private String context;

	@EqualsAndHashCode.Exclude
	@lombok.NonNull
	private String decision;

	@EqualsAndHashCode.Exclude
	@lombok.NonNull
	private String consequences;

	@EqualsAndHashCode.Exclude
	@lombok.NonNull
	@Indexed
	private ADRStatus status;

	@EqualsAndHashCode.Exclude
	@lombok.NonNull
	@DBRef
	private Collection<AccessGroup> accessGroups;

	@EqualsAndHashCode.Exclude
	@Setter(AccessLevel.PRIVATE)
	@Indexed
	private List<ADRTag> tags;

	@EqualsAndHashCode.Exclude
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	@RecursiveSaving
	private List<ExternalContent> externalContents;

	@EqualsAndHashCode.Exclude
	@Setter(AccessLevel.PRIVATE)
	private List<ADRComment> comments;

	@EqualsAndHashCode.Exclude
	@Setter(AccessLevel.NONE)
	private Set<String> supersededIds;

	@PersistenceConstructor
	private ADR(ADRPath fullPath, ADRPath parentPath, ADRStatusType statusType){}

	/**
	 * Create ADR with most important parameters
	 * @param parent Parent ADRContainer
	 * @param name name of the ADR (for path)
	 * @param author Author of ADR
	 * @param title ADR title
	 * @param context The context of the ADR
	 * @param decision The decision that's made by the ADR
	 * @param consequences The consequences that the ADR will have on the project
	 */
	public ADR(@NonNull final ADRContainer parent,
			   @NonNull final String name,
			   @NonNull final String title,
			   @NonNull final User author,
			   @NonNull final String context,
			   @NonNull final String decision,
			   @NonNull final String consequences){

		this.parent = parent;
		this.name = name.trim();
		this.author = author;
		this.title = title;
		this.context = context;
		this.decision = decision;
		this.consequences = consequences;
		this.status = new ADRStatusCreated();
		this.accessGroups = new HashSet<>();
		this.tags = new ArrayList<>();
		this.externalContents = new ArrayList<>();
		this.comments = new ArrayList<>();
		this.supersededIds = new HashSet<>();
	}

	public ADR(@NonNull final ADRContainer parent,
			   @NonNull final String name,
			   @NonNull final ADRStatus status){

		this.parent = parent;
		this.name = name.trim();
		this.author = null;
		this.status = status;
		this.accessGroups = new HashSet<>();
		this.tags = new ArrayList<>();
		this.externalContents = new ArrayList<>();
		this.comments = new ArrayList<>();
		this.supersededIds = new HashSet<>();
	}

	public boolean addAccessGroup(AccessGroup accessGroup) {
		return this.accessGroups.add(accessGroup);
	}

	public boolean removeAccessGroup(AccessGroup accessGroup) {
		return this.accessGroups.remove(accessGroup);
	}

	/**
	 * returns whether the specific user can read this ADR
	 * @param user the user that should be tested
	 * @return true if the user can read this ADR
	 */
	public boolean canRead(User user) {
		return user == null || (!user.equals(this.author) && !user.hasRights(new UserRights(false, false, true, false)) && !this.hasRights(user, new AccessRights(true, false)) && !this.status.isAlwaysAccessible());
	}

	/**
	 * returns whether the specific user can write this ADR
	 * @param user the user that should be tested
	 * @return true if the user can write this ADR
	 */
	public boolean canWrite(User user) {
		return user != null && ((user.equals(this.author) || this.hasRights(user, new AccessRights(false, true))) && this.status.isWritable());
	}

/*	/**
	 * returns whether the specific user can vote on this ADR
	 * @param user the user that should be tested
	 * @return true if the user can vote on this ADR
	 *
	public boolean canVote(@NonNull User user) {
		return (user.equals(this.author) || this.vouser.canManageVotes()) && this.status.isVotable();
	}
*/
	/**
	 * returns whether the specific user can manage the voting on this ADR
	 * @param user the user that should be tested
	 * @return true if the user can manage the voting on this ADR
	 */
	public boolean canStartVoting(User user) {
		return this.status.isVotingStartable() && user != null && (user.equals(this.author) || user.canManageVotes());
	}

	public boolean canPropose(User user) {
		return this.status.isProposable() && user != null && (user.canManageVotes());
	}

	public boolean canInviteVoters(User user) {
		return this.status.isVotable() && user != null && (user.equals(this.author) || user.canManageVotes());
	}

	public boolean canEndVoting(User user) {
		return user != null && (user.canManageVotes() || this.getAuthor().equals(Optional.of(user))) && this.getStatus().isVotable();
	}

	/**
	 * returns whether the specific user can manage the AccessGroups on this ADR
	 * @param user the user that should be tested
	 * @return true if the user can manage the AccessGroups on this ADR
	 * */
	public boolean canEditAccessGroups(User user){
		return user != null && this.getStatus().isWritable() && (user.equals(this.author) || user.canManageAdrAccess());
	}

	private boolean hasRights(@NonNull User user, @NonNull AccessRights accessRights) {
		for(AccessGroup ag : this.accessGroups) {
			if(ag.hasAccessRights(user, accessRights)) {
				return true;
			}
		}
		return false;
	}

	public boolean addTag(@NonNull final ADRTag tag){
		return tags.add(tag);
	}

	public boolean removeTag(@NonNull final ADRTag tag){
		return tags.remove(tag);
	}

	public boolean addExternalContent(@NonNull final ExternalContent content){
		return externalContents.add(content);
	}

	public boolean removeExternalContent(@NonNull final ExternalContent content){
		return externalContents.remove(content);
	}

	public boolean addADRComment(@NonNull final ADRComment comment){
		return comments.add(comment);
	}

	public boolean removeADRComment(@NonNull final ADRComment comment){
		return comments.remove(comment);
	}

	@AccessType(AccessType.Type.PROPERTY)
	public ADRPath getParentPath(){
		return parent.getFullPath();
	}

	private void setParentPath(ADRPath path){
	}

	@AccessType(AccessType.Type.PROPERTY)
	public ADRPath getFullPath(){
		return getParentPath().add(name);
	}

	private void setFullPath(ADRPath parentPath){
	}

	@EqualsAndHashCode.Include
	@AccessType(AccessType.Type.PROPERTY)
	public ADRStatusType getStatusType(){
		return status.getType();
	}

	public Optional<User> getAuthor() {
		if(this.author != null) {
			return Optional.of(this.author);
		}
		return Optional.empty();
	}

	/**
	 * Proposes the adr
	 * @return <li>true, if the adr was changed to proposed</li>  <li>false, if the adr was already in a status in which it can't be proposed</li>
	 */
	public boolean propose() {
		if (this.status.getType().equals(ADRStatusType.CREATED))
		{
			ADRStatusInternallyProposed internallyProposedStatus = new ADRStatusInternallyProposed();
			this.setStatus(internallyProposedStatus);
			ADRReview review = internallyProposedStatus.getAdrReview();

			//this.getAuthor().ifPresent(review::addVoter);

			this.getAccessGroups().forEach(accessGroup -> {
				if (accessGroup.getRights().isVotable()) {
					accessGroup.getUsers().stream()
							.filter(user -> this.getAuthor().isEmpty() || !this.getAuthor().get().equals(user))
							.forEach(review::addVoter);
				}
			});
			return true;
		}

		if(this.status.getType().equals(ADRStatusType.INTERNALLY_PROPOSED)) {
			ADRStatusProposed proposedStatus = new ADRStatusProposed();
			this.setStatus(proposedStatus);
			ADRReview review = proposedStatus.getAdrReview();

			//this.getAuthor().ifPresent(review::addVoter);

			this.getAccessGroups().forEach(accessGroup -> {
				if (accessGroup.getRights().isVotable()) {
					accessGroup.getUsers().stream()
							.filter(user -> this.getAuthor().isEmpty() || !this.getAuthor().get().equals(user))
							.forEach(review::addVoter);
				}
			});
			return true;
		}
		return false;
	}

	public enum VoteResult { NOT_VOTABLE, DECLINED, APPROVED, INTERNALLY_APPROVED }

	/**
	 * end and evaluate the adr-voting
	 */
	public VoteResult endVoting(ADRDao adrDao) {
		if(!this.status.isVotable()) {
			return VoteResult.NOT_VOTABLE;
		}

		return this.status.adrReviewAsOpt().ifPresent(VoteResult.NOT_VOTABLE, review -> {
			Map<VoteType, Integer> results = review.getVoteResult();
			final boolean stsIsProposed = this.status.getType().equals(ADRStatusType.PROPOSED);

			if (results.get(VoteType.FOR)*2 >= review.getInvitedVoters().size() && results.get(VoteType.FOR) != 0) {
				if(stsIsProposed){
					this.setStatus(new ADRStatusApproved(review));
					for (String id : supersededIds) {
						adrDao.findById(id).ifPresent(adr1 -> {
							adr1.setStatus(new ADRStatusSuperseded(adr1.getStatus().getAdrReview(), this));
							adrDao.save(adr1);
						});
					}
					return VoteResult.APPROVED;
				}
				propose();
				return VoteResult.INTERNALLY_APPROVED;
			}
			else {
				this.setStatus(stsIsProposed ? new ADRStatusRefused(review)
											 : new ADRStatusCreated());
				return VoteResult.DECLINED;
			}
		});
	}

	public void setSupersededIds(final Set<String> ids){
		supersededIds.clear();
		ids.stream()
			.filter(id -> this.getId() == null || !this.getId().equals(id))
			.forEach(id -> supersededIds.add(id));
	}

/*	public void addSuperseded(final Stream<String> ids){
		ids
			.filter(id -> !supersededIds.contains(id) && (this.getId() == null || !this.getId().equals(id)))
			.forEach(id -> supersededIds.add(id));
	}

	public void addSuperseded(final ADR ...adrs)
	{
		List<String> ids = supersededIds.stream().map(ADR::getId).toList();
		Arrays.stream(adrs)
				.filter(adr -> !ids.contains(adr.getId()) && !this.getId().equals(adr.getId()))
				.forEach(adr -> supersededIds.add(adr));
	}

	public void removeSuperseded(final ADR ...adrs)
	{
		Arrays.stream(adrs)
				.forEach(adr -> supersededIds.remove(adr));
	}
*/
	public boolean isSuperseded(){
		return getStatus().getType().equals(ADRStatusType.SUPERSEDED);
	}

	public boolean isRefused(){
		return getStatus().getType().equals(ADRStatusType.REFUSED);
	}

	@Override
	public String toString() {
		return "ADR{" +
				"name='" + name + '\'' +
				", title='" + title + '\'' +
				", context='" + context + '\'' +
				", decision='" + decision + '\'' +
				", consequences='" + consequences + '\'' +
				", status=" + status +
				'}';
	}
}
