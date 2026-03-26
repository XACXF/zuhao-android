@echo off
chcp 65001
cls
echo ============================================
echo  AndroidApp 上传工具
echo ============================================
echo.

REM 检查是否安装了 Git
git --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到 Git，请先安装 Git
    echo 下载地址: https://git-scm.com/download/win
    pause
    exit /b 1
)

echo [1/5] 正在初始化 Git 仓库...
cd /d "%~dp0"
git init

echo.
echo [2/5] 正在添加文件...
git add .

echo.
echo [3/5] 正在提交...
git commit -m "Initial commit: Android App for 租号管理系统"

echo.
echo [4/5] 请输入 GitHub 仓库信息
echo.
set /p GITHUB_USER=GitHub 用户名: 
set /p REPO_NAME=仓库名称 (默认: zuhao-android): 
if "%REPO_NAME%"=="" set REPO_NAME=zuhao-android

echo.
echo 正在设置远程仓库...
git remote remove origin 2>nul
git remote add origin https://github.com/%GITHUB_USER%/%REPO_NAME%.git

echo.
echo [5/5] 正在推送到 GitHub...
echo 提示: 当要求输入密码时，请输入你的 GitHub Token
echo.
git branch -M main
git push -u origin main

if errorlevel 1 (
    echo.
    echo [错误] 推送失败，请检查:
    echo 1. GitHub 仓库是否已创建
    echo 2. Token 是否正确
    echo 3. 网络连接是否正常
) else (
    echo.
    echo [成功] 代码已上传到 GitHub!
    echo 仓库地址: https://github.com/%GITHUB_USER%/%REPO_NAME%
)

echo.
pause
