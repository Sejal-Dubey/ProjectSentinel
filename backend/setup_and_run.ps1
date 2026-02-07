
$MavenVersion = "3.9.6"
$MavenUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MavenVersion/apache-maven-$MavenVersion-bin.zip"
$MavenZip = "maven.zip"
$MavenDir = "apache-maven-$MavenVersion"

Write-Host "Checking for Maven..."

if (-not (Test-Path "$MavenDir\bin\mvn.cmd")) {
    Write-Host "Maven not found. Downloading Portable Maven $MavenVersion..."
    Invoke-WebRequest -Uri $MavenUrl -OutFile $MavenZip
    
    Write-Host "Extracting Maven..."
    Expand-Archive -Path $MavenZip -DestinationPath . -Force
    
    Remove-Item $MavenZip
    Write-Host "Maven Configured."
} else {
    Write-Host "Using existing local Maven."
}

$MvnCmd = "$PWD\$MavenDir\bin\mvn.cmd"

# Load .env variables
$EnvFile = "..\.env"
if (Test-Path $EnvFile) {
    Write-Host "Loading environment variables from $EnvFile..."
    Get-Content $EnvFile | ForEach-Object {
        if ($_ -match '^([^#=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [System.Environment]::SetEnvironmentVariable($name, $value, [System.EnvironmentVariableTarget]::Process)
            Write-Host "Loaded Env Var: $name"
        }
    }
} else {
    Write-Host "No .env file found at $EnvFile. Using default/system variables."
}

Write-Host "Starting Backend with: $MvnCmd"
& $MvnCmd spring-boot:run
