#!/bin/bash

# Script de prueba para la API de Registro de Usuarios
# Asegúrate de que la aplicación esté corriendo en http://localhost:8080

BASE_URL="http://localhost:8080/api/users"

echo "========================================="
echo "API de Registro de Usuarios - Pruebas"
echo "========================================="
echo ""

# Test 1: Registro exitoso
echo "Test 1: Registro exitoso"
echo "-------------------------"
curl -X POST ${BASE_URL}/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Rodriguez",
    "email": "juan@rodriguez.org",
    "password": "Hunter2",
    "phones": [
      {
        "number": "1234567",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }' | jq .
echo -e "\n\n"

# Test 2: Email duplicado (409)
echo "Test 2: Email duplicado (debe devolver 409)"
echo "--------------------------------------------"
curl -X POST ${BASE_URL}/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Rodriguez",
    "email": "juan@rodriguez.org",
    "password": "Hunter2",
    "phones": [
      {
        "number": "1234567",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }' | jq .
echo -e "\n\n"

# Test 3: Email con formato inválido (400)
echo "Test 3: Email con formato inválido (debe devolver 400)"
echo "-------------------------------------------------------"
curl -X POST ${BASE_URL}/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pedro Perez",
    "email": "email-invalido",
    "password": "Password1",
    "phones": [
      {
        "number": "9876543",
        "citycode": "2",
        "contrycode": "57"
      }
    ]
  }' | jq .
echo -e "\n\n"

# Test 4: Contraseña con formato inválido (400)
echo "Test 4: Contraseña con formato inválido (debe devolver 400)"
echo "------------------------------------------------------------"
curl -X POST ${BASE_URL}/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Lopez",
    "email": "maria@test.cl",
    "password": "abc",
    "phones": [
      {
        "number": "5555555",
        "citycode": "3",
        "contrycode": "57"
      }
    ]
  }' | jq .
echo -e "\n\n"

# Test 5: Campos obligatorios vacíos (400)
echo "Test 5: Campos obligatorios vacíos (debe devolver 400)"
echo "-------------------------------------------------------"
curl -X POST ${BASE_URL}/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "email": "",
    "password": "",
    "phones": []
  }' | jq .
echo -e "\n\n"

# Test 6: Registro exitoso con otro usuario
echo "Test 6: Registro exitoso con otro usuario"
echo "------------------------------------------"
curl -X POST ${BASE_URL}/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ana Martinez",
    "email": "ana@example.cl",
    "password": "Secure123",
    "phones": [
      {
        "number": "1111111",
        "citycode": "1",
        "contrycode": "57"
      },
      {
        "number": "2222222",
        "citycode": "1",
        "contrycode": "57"
      }
    ]
  }' | jq .
echo -e "\n\n"

echo "========================================="
echo "Pruebas completadas"
echo "========================================="

