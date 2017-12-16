package client

import org.webscene.client.html.ParentHtmlElement
import org.webscene.client.html.HtmlCreator as html

internal fun createUserInfoSection(user: User) = html.parentElement("div") {
    classes += "user-info"
    if (user.avatarUrl.isNotEmpty()) children += createAvatarImage(user)
    parentHtmlElement("div") {
        classes += "ser-info__details"
        parentHtmlElement("div") {
            parentHtmlElement("i") { classes.addAll(arrayOf("fa", "fa-fw", "fa-user")) }
            parentHtmlElement("span") { +user.login }
            if (user.fullName.isNotEmpty()) parentHtmlElement("small") { +user.fullName }
        }
        children += createPublicReposLayout(user)
        children += createCreatedAtLayout(user)
        addEmail(this, user)
        if (user.company.isNotEmpty()) children += createCompanyLayout(user)
        children += createProfileLinkLayout(user)
    }
    children += createCommitsLayout()
}

private fun addEmail(parent: ParentHtmlElement, user: User) {
    if (user.email.isNotEmpty()) {
        parent.children += html.parentElement("i") {
            classes.addAll(arrayOf("fa", "fa-fw", "fa-envelope"))
            +user.email
        }
    }
}

private fun createCreatedAtLayout(user: User) = html.parentElement("div") {
    parentHtmlElement("i") { classes.addAll(arrayOf("fa", "fa-fw", "fa-clock-o")) }
    // TODO: Complete this function.
    // Joined GitHub {{ moment(user.createdAt).fromNow() }}
}

private fun createPublicReposLayout(user: User) = html.parentElement("div") {
    parentHtmlElement("i") { classes.addAll(arrayOf("fa", "fa-fw", "fa-database")) }
    parentHtmlElement("span") { +"${user.publicRepos} public repos" }
}

private fun createCompanyLayout(user: User) = html.parentElement("div") {
    parentHtmlElement("i") { classes.addAll(arrayOf("fa", "fa-fw", "fa-building")) }
    parentHtmlElement("span") { +user.company }
}

private fun createProfileLinkLayout(user: User) = html.parentElement("div") {
    parentHtmlElement("i") { classes.addAll(arrayOf("fa", "fa-fw", "fa-external-link")) }
    parentHtmlElement("a") {
        attributes["href"] = user.profileUrl
        attributes["target"] = "_blank"
        +"View profile on GitHub"
    }
}

private fun createCommitsLayout() = html.parentElement("div") {
    classes.addAll(arrayOf("chart-container", "commits-per-quarter"))
    parentHtmlElement("canvas") { id = "quarterCommitCount" }
}

private fun createAvatarImage(user: User) = html.element("img") {
    classes += "user-info__img"
    attributes["alt"] = user.login
    attributes["src"] = user.avatarUrl
}