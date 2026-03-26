@echo off
chcp 65001
cls
echo ============================================
echo  租号管理系统 - GitHub上传工具
echo ============================================
echo.

REM 检查Python是否安装
python --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未检测到Python，请先安装Python 3.x
    echo 下载地址: https://www.python.org/downloads/
    pause
    exit /b 1
)

echo 正在启动上传程序...
echo.

REM 运行Python脚本
python upload_to_github.py
