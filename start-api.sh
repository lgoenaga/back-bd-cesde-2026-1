#!/bin/bash
# Script para iniciar la aplicación REST API

echo "╔════════════════════════════════════════════════════════════╗"
echo "║   Student Information System - REST API                    ║"
echo "║   Starting Spring Boot Application...                      ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Cargar variables de entorno desde archivo .env si existe
if [ -f .env ]; then
    echo "📋 Cargando variables de entorno desde .env..."
    export $(cat .env | grep -v '^#' | grep -v '^$' | xargs)
    echo "✅ Variables de entorno cargadas"
    echo ""
else
    echo "⚠️  Archivo .env no encontrado. Usando valores por defecto."
    echo "   Recomendación: Copiar .env.example a .env y configurar."
    echo ""
fi

# Verificar que Maven esté instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado. Por favor instálelo primero."
    exit 1
fi

# Verificar que Java esté instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java no está instalado. Por favor instálelo primero."
    exit 1
fi

echo "✅ Verificando versión de Java..."
java -version

echo ""
echo "📦 Compilando y empaquetando la aplicación..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Compilación exitosa!"
    echo ""
    echo "🚀 Iniciando la aplicación en http://localhost:${SERVER_PORT:-8080}${CONTEXT_PATH:-/api}"
    echo ""
    echo "📚 Endpoints health:"
    echo "   - GET  http://localhost:${SERVER_PORT:-8080}${CONTEXT_PATH:-/api}/health"

    echo ""
    echo "Presiona Ctrl+C para detener la aplicación"
    echo ""

    java -jar target/student-information-system-1.0.0.jar
else
    echo ""
    echo "❌ Error al compilar la aplicación"
    exit 1
fi

