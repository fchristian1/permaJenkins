import jenkins.model.*
import java.util.logging.Logger

def logger = Logger.getLogger("")
def instance = Jenkins.getInstance()
def pluginManager = instance.getPluginManager()
def updateCenter = instance.getUpdateCenter()

// Warten, bis das Update Center vollständig initialisiert ist
updateCenter.updateAllSites()

def pluginsToInstall = ["role-strategy"]
def installedPlugins = pluginManager.plugins.collect { it.getShortName() }

def collectDependencies(pluginName, collectedDependencies, updateCenter) {
    logger.info("Collecting dependencies for '${pluginName}' plugin...")
    def pluginInfo = updateCenter.getPlugin(pluginName)
    if (pluginInfo != null) {
        pluginInfo.dependencies.each { dependency ->
            def dependencyName = dependency.shortName ?: dependency.plugin?.name
            if (dependencyName) {
                if (!collectedDependencies.contains(dependencyName)) {
                    collectedDependencies.add(dependencyName)
                    collectDependencies(dependencyName, collectedDependencies, updateCenter)
                }
            } else {
                logger.warning("Could not determine the name of the dependency for '${pluginName}'")
            }
        }
    } else {
        logger.warning("Could not find plugin info for '${pluginName}'")
    }
}

def installPlugin(pluginName, installedPlugins, logger, updateCenter) {
    logger.info("Checking if '${pluginName}' plugin is installed...")
    if (!installedPlugins.contains(pluginName)) {
        logger.info("Installing '${pluginName}' plugin...")
        def pluginDeployment = null
        while (pluginDeployment == null) {
            def pluginInfo = updateCenter.getPlugin(pluginName)
            if (pluginInfo != null) {
                pluginDeployment = pluginInfo.deploy()
            } else {
                logger.info("Waiting for '${pluginName}' plugin to become available...")
                sleep(10000) // 10 Sekunden warten
            }
        }
        pluginDeployment.get()
        logger.info("'${pluginName}' plugin installed.")
        installedPlugins.add(pluginName)
    } else {
        logger.info("'${pluginName}' plugin is already installed.")
    }
}

pluginsToInstall.each { pluginName ->
    def collectedDependencies = []
    collectDependencies(pluginName, collectedDependencies, updateCenter)
    collectedDependencies.reverse().each { dependencyName ->
        installPlugin(dependencyName, installedPlugins, logger, updateCenter)
    }
    installPlugin(pluginName, installedPlugins, logger, updateCenter)
}

instance.save()
instance.restart()