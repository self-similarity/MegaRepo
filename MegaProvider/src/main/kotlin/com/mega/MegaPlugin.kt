package com.mega

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.mapper
import com.lagradost.cloudstream3.plugins.RepositoryManager
import com.lagradost.cloudstream3.ui.settings.extensions.RepositoryData
import com.lagradost.cloudstream3.utils.Coroutines.ioSafe

@CloudstreamPlugin
class MegaPlugin : Plugin() {
    override fun load(context: Context) {
        ioSafe {
            val repositories = getRepositories()
            addRepositories(repositories)
        }
    }

    private suspend fun addRepositories(repositories: List<String>) {
        val addedRepositories = RepositoryManager.getRepositories()
        repositories.forEach { url ->
            // Early exit for already added repos
            if (addedRepositories.any { it.url == url }) return@forEach

            val repo = RepositoryManager.parseRepository(url)
            val name = repo?.name ?: "No name"
            val data = RepositoryData(name, url)
            RepositoryManager.addRepository(data)
        }
    }

    private suspend fun getRepositories(): List<String> {
        data class VerifiedRepo(
            val url: String? = null,
            val verified: Boolean? = null
        )

        val text =
            app.get("https://raw.githubusercontent.com/recloudstream/cs-repos/master/repos-db.json").text

        val tree = ObjectMapper().readTree(text)

        return tree.mapNotNull {
            when (it) {
                is TextNode -> mapper.treeToValue<String>(it)
                is ObjectNode -> mapper.treeToValue<VerifiedRepo>(it).url
                else -> null
            }
        }
    }
}