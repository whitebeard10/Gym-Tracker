$componentsFile = "components_list.txt"
$stateFile = "push_state.txt"
$workingDir = Get-Location

if (!(Test-Path $componentsFile) -or !(Test-Path $stateFile)) {
    Write-Error "Required files (components_list.txt or push_state.txt) not found."
    exit 1
}

$components = Get-Content $componentsFile
$state = [int](Get-Content $stateFile)

if ($state -ge $components.Count) {
    Write-Host "All components have been pushed."
    exit 0
}

$currentComponentPath = $components[$state]
$componentName = Split-Path $currentComponentPath -Leaf

Write-Host "Pushing component: $componentName ($currentComponentPath)"

# Change directory to the workspace root
Set-Location $workingDir

# Git operations
git add $currentComponentPath
if ($LASTEXITCODE -ne 0) {
    Write-Error "Failed to add component path: $currentComponentPath"
    exit 1
}

$commitMessage = "Day $($state + 2): Pushing component $componentName"
git commit -m $commitMessage
if ($LASTEXITCODE -ne 0) {
    # If there's nothing to commit, just proceed
    Write-Host "Nothing to commit for $componentName, incrementing state anyway."
}

git push origin master
if ($LASTEXITCODE -ne 0) {
    Write-Error "Failed to push to GitHub. Please check your internet connection or git credentials."
    exit 1
}

# Update state
$newState = $state + 1
$newState | Out-File $stateFile -Encoding utf8

Write-Host "Successfully pushed $componentName. Next component index: $newState"
