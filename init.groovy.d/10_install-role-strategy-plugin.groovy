import jenkins.model.*
import java.util.logging.Logger

def logger = Logger.getLogger("")
def instance = Jenkins.getInstance()
def pluginManager = instance.getPluginManager()
def updateCenter = instance.getUpdateCenter()

// Warten, bis das Update Center vollständig initialisiert ist
updateCenter.updateAllSites()

def plugin = pluginManager.getPlugin("role-strategy")
if (plugin == null) {
    logger.info("Installing 'role-strategy' plugin...")
    def pluginDeployment = null
    while (pluginDeployment == null) {
        def pluginInfo = updateCenter.getPlugin("role-strategy")
        if (pluginInfo != null) {
            pluginDeployment = pluginInfo.deploy()
        } else {
            logger.info("Waiting for 'role-strategy' plugin to become available...")
            sleep(10000) // 10 Sekunden warten
        }
    }
    pluginDeployment.get()
    logger.info("'role-strategy' plugin installed.")
    instance.save()
    instance.restart()
} else {
    logger.info("'role-strategy' plugin is already installed.")
}