version = "0.0.6"

project.extra["PluginName"] = "S-1D Fletcher"
project.extra["PluginDescription"] = "Automate fletching, select custom task or allow the plugin to choose the best task for you."


tasks {
    jar {
        manifest {
            attributes(mapOf(
                "Plugin-Version" to project.version,
                "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                "Plugin-Provider" to project.extra["PluginProvider"],
                "Plugin-Description" to project.extra["PluginDescription"],
                "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}
