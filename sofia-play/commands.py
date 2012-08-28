from subprocess import call
import string
import os

# Here you can create play commands that are specific to the module, and extend existing commands

MODULE = 'sofia'

# Commands that are specific to your module

COMMANDS = ['sofia:generate']

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command == "sofia:generate":
        print "~ Generating sofia resources"
        module = ""
        for i in app.modules():
            if i.find(MODULE) != -1:
                module = i

        jars = env['basedir'] + "/framework/play-" + env['version'] + ".jar"
        for jar in os.listdir(module + "/lib"):
            jars += ":" + module + "/lib/" + jar
        call([app.java_path(), "-cp", jars, "com.antwerkz.sofia.play.SofiaPlugin"])


# This will be executed before any command (new, run...)
def before(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")


# This will be executed after any command (new, run...)
def after(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")
