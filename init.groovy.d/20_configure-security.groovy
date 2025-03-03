import jenkins.model.*
import hudson.security.*
import java.util.logging.Logger
import java.lang.reflect.Method

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
Jenkins jenkins = Jenkins.get()
def rbas = new RoleBasedAuthorizationStrategy()

// Überprüfen, ob der Admin-Benutzer bereits existiert
def adminUser = hudsonRealm.getUser("admin")

if (adminUser == null) {
    hudsonRealm.createAccount("admin", "admin")
    println("Admin-Benutzer 'admin' mit Passwort 'admin' wurde erfolgreich erstellt!")
} else {
    println("Admin-Benutzer 'admin' existiert bereits. Keine Änderungen vorgenommen.")
}

Set<Permission> permissions = new HashSet<>();
permissions.add(Jenkins.ADMINISTER)
Role adminRole = new Role("admin", permissions)

globalRoleMap = rbas.getRoleMap(RoleType.Global)
globalRoleMap.addRole(adminRole)
/* assign admin role to user 'admin' */
globalRoleMap.assignRole(adminRole, new PermissionEntry(AuthorizationType.USER, 'admin'))
/* assign admin role to group 'administrators' */
globalRoleMap.assignRole(adminRole, new PermissionEntry(AuthorizationType.GROUP, 'administrators'))
jenkins.setAuthorizationStrategy(rbas)

jenkins.save()