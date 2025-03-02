import jenkins.model.*
import hudson.security.*
import jenkins.install.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import hudson.model.User

def instance = Jenkins.getInstance()

// Sicherheitsrealm setzen (Benutzer und Passwort)
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
instance.setSecurityRealm(hudsonRealm)

// Überprüfen, ob der Admin-Benutzer bereits existiert
def adminUser = User.get("admin", false, null)

if (adminUser == null) {
    hudsonRealm.createAccount("admin", "admin")
    println("Admin-User 'admin' mit Passwort 'admin' wurde erfolgreich erstellt!")
} else {
    println("Admin-User 'admin' existiert bereits. Keine Änderungen vorgenommen.")
}

// Rollenbasierte Berechtigungen setzen
def roleBasedStrategy = new RoleBasedAuthorizationStrategy()
instance.setAuthorizationStrategy(roleBasedStrategy)

// Admin-Rolle erstellen und Berechtigungen zuweisen
def permissions = [
    hudson.model.Hudson.ADMINISTER,
    // Weitere Berechtigungen können hier hinzugefügt werden
]
def adminRole = new Role("admin", ".*", permissions)
roleBasedStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole)
roleBasedStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole, Collections.singleton("admin"))

// Änderungen speichern
instance.save()
