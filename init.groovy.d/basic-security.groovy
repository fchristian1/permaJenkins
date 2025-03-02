#!groovy

import jenkins.model.*
import hudson.security.*
import hudson.util.*
import jenkins.install.*
import com.michelin.cio.hudson.plugins.rolestrategy.*

def instance = Jenkins.getInstance()

// Überprüfen, ob der Admin-Benutzer bereits existiert
def hudsonRealm = instance.getSecurityRealm()
def adminUser = hudsonRealm.getUser("admin")
instance.setSecurityRealm(hudsonRealm)

// Rollenbasierte Berechtigungen setzen
def roleBasedStrategy = new RoleBasedAuthorizationStrategy()
instance.setAuthorizationStrategy(roleBasedStrategy)

if (adminUser == null) {
    // Sicherheitsrealm setzen (Benutzer und Passwort)
    hudsonRealm = new HudsonPrivateSecurityRealm(false)
    hudsonRealm.createAccount("admin", "admin")

    // Admin-Rolle erstellen und Berechtigungen zuweisen
    def adminRole = new Role("admin", ".*", [Permission.all()])
    roleBasedStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole)
    roleBasedStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole, "admin")

    // Änderungen speichern
    instance.save()

    println("Admin-User 'admin' mit Passwort 'admin' wurde erfolgreich erstellt und Rollenbasierte Berechtigungen gesetzt!")
} else {
    println("Admin-User 'admin' existiert bereits. Keine Änderungen vorgenommen.")
}
