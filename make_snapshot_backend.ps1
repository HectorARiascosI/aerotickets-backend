# === Snapshot solo con archivos versionados por Git, evitando bloqueos ===
$ErrorActionPreference = 'Stop'

# 1) Archivo de salida en %TEMP%
$stamp = (Get-Date).ToString('yyyyMMdd_HHmmss')
$tmpOut = Join-Path $env:TEMP "backend_snapshot_$stamp.txt"

# 2) Extensiones binarias a omitir
$binaryExt = @(
  '.png','.jpg','.jpeg','.gif','.webp','.ico','.svg',
  '.woff','.woff2','.ttf','.eot',
  '.zip','.gz','.br','.7z','.rar',
  '.pdf','.mp4','.mp3','.webm','.mov',
  '.class'
)

# 3) Obtener lista de archivos versionados por Git
#    (si el repo no es Git, cae al modo filesystem)
function Get-VersionedFiles {
  try {
    git rev-parse --is-inside-work-tree *> $null
    $files = git ls-files
    if (-not $files) { throw "No files from git." }
    return $files
  } catch {
    # Fallback: recorre disco, excluyendo rutas típicas
    $exclude = '(\\(node_modules|dist|build|coverage|logs|target|out|\.git|\.idea|\.vscode|\.settings)(\\|$))|(\\\.env($|\\|\.))'
    return Get-ChildItem -Recurse -File -Force `
      | Where-Object { $_.FullName -notmatch $exclude } `
      | ForEach-Object { $_.FullName }
  }
}

$files = Get-VersionedFiles

# 4) Encabezado: árbol simple
"### PROJECT TREE" | Set-Content $tmpOut -Encoding UTF8
$files `
| ForEach-Object {
    # Normaliza a ruta relativa si se puede
    try {
      $rel = Resolve-Path -LiteralPath $_ -Relative
    } catch { $rel = $_ }
    "      $rel"
  } `
| Sort-Object `
| Add-Content $tmpOut -Encoding UTF8

# 5) Contenido de archivos
"`n`n### FILE CONTENTS" | Add-Content $tmpOut -Encoding UTF8

foreach($f in $files) {
  try {
    $ext = [IO.Path]::GetExtension($f)
    if ($ext) { $ext = $ext.ToLower() } else { $ext = '' }
    if ($binaryExt -contains $ext) { continue }

    try { $rel = Resolve-Path -LiteralPath $f -Relative } catch { $rel = $f }

    "`n`n===== FILE: $rel =====" | Add-Content $tmpOut -Encoding UTF8
    Get-Content -Raw -LiteralPath $f -Encoding UTF8 `
      | Add-Content $tmpOut -Encoding UTF8
  }
  catch {
    "`n`n===== FILE: $f =====" | Add-Content $tmpOut -Encoding UTF8
    "[[WARN]] No se pudo leer este archivo: $($_.Exception.Message)" `
      | Add-Content $tmpOut -Encoding UTF8
  }
}

# 6) Copiamos el resultado al repo con nombre único (evita locks)
$finalName = "backend_snapshot_$stamp.txt"
Copy-Item -LiteralPath $tmpOut -Destination $finalName -Force

Write-Host "OK: $finalName"