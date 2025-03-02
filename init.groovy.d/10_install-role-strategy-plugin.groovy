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

def installPluginWithDependencies(pluginName) {
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

        // Installiere Abhängigkeiten rekursiv
        def pluginInfo = updateCenter.getPlugin(pluginName)
        if (pluginInfo != null) {
            pluginInfo.dependencies.each { dependency ->
                installPluginWithDependencies(dependency.shortName)
            }
        }
    } else {
        logger.info("'${pluginName}' plugin is already installed.")
    }
}

pluginsToInstall.each { pluginName ->
    installPluginWithDependencies(pluginName)
}

instance.save()
instance.restart()