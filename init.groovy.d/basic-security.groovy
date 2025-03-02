import jenkins.model.*
import hudson.security.*
import com.michelin.cio.hudson.plugins.rolestrategy.*

def instance = Jenkins.getInstance()

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
