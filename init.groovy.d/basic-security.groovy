#!groovy

import jenkins.model.*
import hudson.security.*
import hudson.util.*
import jenkins.install.*

// Jenkins-Instanz abrufen
def instance = Jenkins.getInstance()

// Überprüfen, ob der Admin-Benutzer bereits existiert
def hudsonRealm = instance.getSecurityRealm()
def adminUser = hudsonRealm.getUser("admin")

if (adminUser == null) {
    // Sicherheitsrealm setzen (Benutzer und Passwort)
    hudsonRealm = new HudsonPrivateSecurityRealm(false)
    hudsonRealm.createAccount("admin", "admin")
    instance.setSecurityRealm(hudsonRealm)

    // Berechtigungen setzen
    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    strategy.setAllowAnonymousRead(false)
    instance.setAuthorizationStrategy(strategy)

    // Änderungen speichern
    instance.save()

    println("Admin-User 'admin' mit Passwort 'admin' wurde erfolgreich erstellt!")
} else {
    println("Admin-User 'admin' existiert bereits. Keine Änderungen vorgenommen.")
}
