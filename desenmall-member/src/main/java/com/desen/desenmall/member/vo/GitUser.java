package com.desen.desenmall.member.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class GitUser {

    private String accessToken;

    @JSONField(name="login")
    private String login;

    @JSONField(name="id")
    private Integer id;

    @JSONField(name="node_id")
    private String nodeId;

    @JSONField(name="avatar_url")
    private String avatarUrl;

    @JSONField(name="gravatar_id")
    private String gravatarId;

    @JSONField(name="url")
    private String url;

    @JSONField(name="html_url")
    private String htmlUrl;

    @JSONField(name="followers_url")
    private String followersUrl;

    @JSONField(name="following_url")
    private String followingUrl;

    @JSONField(name="gists_url")
    private String gistsUrl;

    @JSONField(name="starred_url")
    private String starredUrl;

    @JSONField(name="subscriptions_url")
    private String subscriptionsUrl;

    @JSONField(name="organizations_url")
    private String organizationsUrl;

    @JSONField(name="repos_url")
    private String reposUrl;

    @JSONField(name="events_url")
    private String eventsUrl;

    @JSONField(name="received_events_url")
    private String receivedEventsUrl;

    @JSONField(name="type")
    private String type;

    @JSONField(name="site_admin")
    private Boolean siteAdmin;

    @JSONField(name="name")
    private String name;

    @JSONField(name="company")
    private String company;

    @JSONField(name="blog")
    private String blog;

    @JSONField(name="location")
    private String location;

    @JSONField(name="email")
    private String email;

    @JSONField(name="hireable")
    private String hireable;

    @JSONField(name="bio")
    private String bio;

    @JSONField(name="twitter_username")
    private String twitterUsername;

    @JSONField(name="public_repos")
    private Integer publicRepos;

    @JSONField(name="public_gists")
    private Integer publicGists;

    @JSONField(name="followers")
    private Integer followers;

    @JSONField(name="following")
    private Integer following;

    @JSONField(name="created_at")
    private String createdAt;

    @JSONField(name="updated_at")
    private String updatedAt;
}
