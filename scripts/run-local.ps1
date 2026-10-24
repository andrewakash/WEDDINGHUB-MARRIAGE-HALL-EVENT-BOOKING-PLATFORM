$ErrorActionPreference = 'Stop'

function ConvertFrom-SecureStringToPlainText([System.Security.SecureString]$Value) {
    $pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($Value)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)
    }
    finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
    }
}

$dbSecret = Read-Host 'Local MySQL password' -AsSecureString
$adminEmail = Read-Host 'Admin account email'
$adminSecret = Read-Host 'New admin password (12-72 UTF-8 bytes)' -AsSecureString
$adminPassword = ConvertFrom-SecureStringToPlainText $adminSecret

if ([string]::IsNullOrWhiteSpace($adminEmail)) {
    throw 'Admin email cannot be empty.'
}
$passwordBytes = [Text.Encoding]::UTF8.GetByteCount($adminPassword)
if ($passwordBytes -lt 12 -or $passwordBytes -gt 72) {
    throw 'Admin password must be 12-72 bytes when encoded as UTF-8.'
}

$env:WEDDINGHUB_DB_PASSWORD = ConvertFrom-SecureStringToPlainText $dbSecret
$env:WEDDINGHUB_ADMIN_EMAIL = $adminEmail.Trim()
$env:WEDDINGHUB_ADMIN_PASSWORD = $adminPassword
$env:WEDDINGHUB_ADMIN_FULL_NAME = 'WeddingHub Admin'

try {
    & (Join-Path $PSScriptRoot '..\mvnw.cmd') spring-boot:run
    exit $LASTEXITCODE
}
finally {
    Remove-Item Env:WEDDINGHUB_DB_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:WEDDINGHUB_ADMIN_EMAIL -ErrorAction SilentlyContinue
    Remove-Item Env:WEDDINGHUB_ADMIN_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:WEDDINGHUB_ADMIN_FULL_NAME -ErrorAction SilentlyContinue
    $adminPassword = $null
    $dbSecret = $null
    $adminSecret = $null
}
