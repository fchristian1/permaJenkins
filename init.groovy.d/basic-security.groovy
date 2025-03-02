#!groovy

import jenkins.model.*
import hudson.security.*
import hudson.util.*
import jenkins.install.*

// Jenkins-Instanz abrufen
def instance = Jenkins.getInstance()

// Sicherheitsrealm setzen (Benutzer und Passwort)
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "admin")
instance.setSecurityRealm(hudsonRealm)

// Berechtigungen setzen
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

// Änderungen speichern
instance.save()

println("Admin-User 'admin' mit Passwort 'admin' wurde erfolgreich erstellt!")
