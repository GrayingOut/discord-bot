
# Download maven dependencies
mvn clean dependency:copy-dependencies

# Delete last compiled source, if exists
if ((Test-Path -Path bin) -eq $true) {
    Remove-Item bin/* -Recurse -Force
}

# Compile the java source files into the bin folder
javac -cp "src/main/java;target/dependency/*" src/main/java/me/grayingout/App.java -d bin

# Run the compiled sources
java -cp "bin;target/dependency/*" me.grayingout.App
