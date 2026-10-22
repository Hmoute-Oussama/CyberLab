# CyberLab

CyberLab is a small Java SE command-line toolkit for learning about cybersecurity.

## Requirements

- Java Development Kit (JDK) 21 or later

## Run

From the project root, compile the source files and start the application:

```powershell
New-Item -ItemType Directory -Force bin
javac -d bin src\cyberlab\*.java
java -cp bin cyberlab.Main
```

## Available tools

- **Password Analyzer** — checks password length and character variety, flags a small set of common passwords and predictable patterns, and provides a heuristic score with recommendations. The score is not a guarantee of password security.
- **Password Generator** — planned.
- **File Integrity Checker** — planned.
- **Log Analyzer** — planned.

Choose an option from the menu to use a tool, or enter `0` to exit.
