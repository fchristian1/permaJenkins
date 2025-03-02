import jenkins.model.*
import hudson.security.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import java.util.logging.Logger

def logger = Logger.getLogger("")
def instance = Jenkins.getInstance()
def pluginManager = instance.getPluginManager()
def updateCenter = instance.getUpdateCenter()

// Überprüfen, ob das 'role-strategy' Plugin installiert ist
def plugin = pluginManager.getPlugin("role-strategy")
if (plugin == null) {
    logger.info("Installing 'role-strategy' plugin...")
    def pluginDeployment = updateCenter.getPlugin("role-strategy").deploy()
    pluginDeployment.get()
    logger.info("'role-strategy' plugin installed.")
    instance.save()
    instance.restart()
    return
} else {
    logger.info("'role-strategy' plugin is already installed.")
}
if (plugin == null) {
    // Sicherheitsrealm setzen (Benutzer und Passwort)
    def hudsonRealm = new HudsonPrivateSecurityRealm(false)
    instance.setSecurityRealm(hudsonRealm)

    // Überprüfen, ob der Admin-Benutzer bereits existiert
    def adminUser = hudsonRealm.getUser("admin")

    if (adminUser == null) {
        hudsonRealm.createAccount("admin", "admin")
        println("Admin-Benutzer 'admin' mit Passwort 'admin' wurde erfolgreich erstellt!")
    } else {
        println("Admin-Benutzer 'admin' existiert bereits. Keine Änderungen vorgenommen.")
    }

    // Rollenbasierte Berechtigungen setzen
    def roleBasedStrategy = new RoleBasedAuthorizationStrategy()
    instance.setAuthorizationStrategy(roleBasedStrategy)

    // Admin-Rolle erstellen und Berechtigungen zuweisen
    def permissions = new HashSet<Permission>()
    permissions.add(Jenkins.ADMINISTER)
    def adminRole = new Role("admin", ".*", permissions)
    roleBasedStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole)
    roleBasedStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole, "admin")

    // Änderungen speichern
instance.save()
}
