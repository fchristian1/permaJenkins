import jenkins.model.*
import hudson.security.*
import java.util.logging.Logger

def logger = Logger.getLogger("")
def instance = Jenkins.getInstance()
def pluginManager = instance.getPluginManager()

// Überprüfen, ob das 'role-strategy' Plugin installiert ist
def plugin = pluginManager.getPlugin("role-strategy")
if (plugin == null || !plugin.isActive()) {
    logger.warning("'role-strategy' plugin is not installed or not active. Skipping security configuration.")
    return
}
sleep(10000) // 10 Sekunden warten
import com.michelin.cio.hudson.plugins.rolestrategy.AuthorizationType
import com.michelin.cio.hudson.plugins.rolestrategy.PermissionEntry
import com.michelin.cio.hudson.plugins.rolestrategy.RoleBasedAuthorizationStrategy
import com.michelin.cio.hudson.plugins.rolestrategy.Role
import com.synopsys.arc.jenkins.plugins.rolestrategy.RoleType
import hudson.security.Permission
import jenkins.model.Jenkins

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
import com.michelin.cio.hudson.plugins.rolestrategy.*

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