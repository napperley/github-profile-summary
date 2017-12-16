package client

import org.webscene.client.html.ParentHtmlTag
import org.webscene.client.html.HtmlCreator as html

internal fun createUserPage(user: User): ParentHtmlTag = html.parentElement("div") {
    children += createUserInfoSection(user)
    children += createChartsLayout()
    children += createLoadScreen()
}

private fun createChartsLayout() = html.parentElement("div") {
    classes += "charts"
    children += createRowOne()
    children += createRowTwo()
}

private fun createRowOne() = html.parentElement("div") {
    classes += "chart-row"
    parentHtmlElement("div") {
        classes.addAll(arrayOf("chart-container", "chart-container--third"))
        parentHtmlElement("h2") { +"Repos per Language" }
        parentHtmlElement("canvas") { id = "langRepoCount" }
    }
    // TODO: Complete this function.
//    <div v-if="Object.keys(data.langStarCount).length > 0" class="chart-container chart-container--third">
//    <h2>Stars per Language</h2>
//    <canvas id="langStarCount"></canvas>
//    </div>
//    <div class="chart-container chart-container--third">
//    <h2>Commits per Language</h2>
//    <canvas id="langCommitCount"></canvas>
//    </div>
}

private fun createRowTwo() = html.parentElement("div") {
    classes += "chart-row"

    // TODO: Complete this function.
    parentHtmlElement("div") {
        classes.addAll(arrayOf("chart-container", "chart-container--half"))
        parentHtmlElement("h2") {
            parentHtmlElement("span") { +"Commits per Repo" }
            //    <small v-if="Object.keys(data.repoCommitCount).length === 10">(top 10)</small>
        }
        parentHtmlElement("canvas") { id = "repoCommitCount" }
    }
//    <div v-if="Object.keys(data.repoStarCount).length > 0" class="chart-container chart-container--half">
//    <h2>Stars per Repo
//    <small v-if="Object.keys(data.repoStarCount).length == 10">(top 10)</small>
//    </h2>
//    <canvas id="repoStarCount"></canvas>
//    </div>
}

private fun createLoadScreen() = html.parentElement("div") {
    classes += "load-screen"
    parentHtmlElement("div") {
        classes.addAll(arrayOf("la-square-jelly-box", "la-3x"))
        parentHtmlElement("div") {}
        parentHtmlElement("div") {}
    }
    parentHtmlElement("h2") { +"Analyzing GitHub profile" }
}