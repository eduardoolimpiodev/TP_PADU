#!/bin/bash

# User Data Processor API Test Script
# This script tests all the main API endpoints

BASE_URL="http://localhost:8080/api"
EXAMPLES_DIR="../examples"

echo "🚀 Starting User Data Processor API Tests"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ $2${NC}"
    else
        echo -e "${RED}❌ $2${NC}"
    fi
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Test 1: Health Check
echo
print_info "Test 1: Health Check"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/health")
if [ "$response" = "200" ]; then
    print_status 0 "Health check passed"
else
    print_status 1 "Health check failed (HTTP $response)"
fi

# Test 2: System Info
echo
print_info "Test 2: System Information"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/info")
print_status $? "System info endpoint"

# Test 2.1: Swagger UI
echo
print_info "Test 2.1: Swagger UI"
response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/swagger-ui.html")
print_status $? "Swagger UI endpoint (HTTP $response)"

# Test 2.2: OpenAPI Docs
echo
print_info "Test 2.2: OpenAPI Documentation"
response=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/v3/api-docs")
print_status $? "OpenAPI docs endpoint (HTTP $response)"

# Test 3: Supported Types
echo
print_info "Test 3: Supported Types"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/supported-types")
print_status $? "Supported types endpoint"

# Test 4: Upload CSV File
echo
print_info "Test 4: Upload CSV File"
if [ -f "$EXAMPLES_DIR/users.csv" ]; then
    response=$(curl -s -o /dev/null -w "%{http_code}" \
        -X POST \
        -F "file=@$EXAMPLES_DIR/users.csv" \
        -F "fileType=csv" \
        "$BASE_URL/users/upload")
    print_status $? "CSV file upload (HTTP $response)"
else
    print_warning "CSV example file not found"
fi

# Test 5: Upload JSON File
echo
print_info "Test 5: Upload JSON File"
if [ -f "$EXAMPLES_DIR/users.json" ]; then
    response=$(curl -s -o /dev/null -w "%{http_code}" \
        -X POST \
        -F "file=@$EXAMPLES_DIR/users.json" \
        -F "fileType=json" \
        "$BASE_URL/users/upload")
    print_status $? "JSON file upload (HTTP $response)"
else
    print_warning "JSON example file not found"
fi

# Test 6: Upload XML File
echo
print_info "Test 6: Upload XML File"
if [ -f "$EXAMPLES_DIR/users.xml" ]; then
    response=$(curl -s -o /dev/null -w "%{http_code}" \
        -X POST \
        -F "file=@$EXAMPLES_DIR/users.xml" \
        -F "fileType=xml" \
        "$BASE_URL/users/upload")
    print_status $? "XML file upload (HTTP $response)"
else
    print_warning "XML example file not found"
fi

# Test 7: Get Users (JSON)
echo
print_info "Test 7: Get Users (JSON format)"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/users?format=json&page=0&size=5")
print_status $? "Get users in JSON format (HTTP $response)"

# Test 8: Get Users (CSV)
echo
print_info "Test 8: Get Users (CSV format)"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/users?format=csv")
print_status $? "Get users in CSV format (HTTP $response)"

# Test 9: Get Users (XML)
echo
print_info "Test 9: Get Users (XML format)"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/users?format=xml")
print_status $? "Get users in XML format (HTTP $response)"

# Test 10: Get Users by Source
echo
print_info "Test 10: Get Users by Source"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/users/source/csv")
print_status $? "Get users by source (HTTP $response)"

# Test 11: Statistics
echo
print_info "Test 11: General Statistics"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/statistics")
print_status $? "General statistics (HTTP $response)"

# Test 12: Source Statistics
echo
print_info "Test 12: Source Statistics"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/statistics/sources")
print_status $? "Source statistics (HTTP $response)"

# Test 13: Invalid File Type (should fail)
echo
print_info "Test 13: Invalid File Type (Expected to fail)"
response=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST \
    -F "file=@$EXAMPLES_DIR/users.csv" \
    -F "fileType=invalid" \
    "$BASE_URL/users/upload")
if [ "$response" = "400" ]; then
    print_status 0 "Invalid file type properly rejected (HTTP $response)"
else
    print_status 1 "Invalid file type not properly rejected (HTTP $response)"
fi

# Test 14: Missing File (should fail)
echo
print_info "Test 14: Missing File (Expected to fail)"
response=$(curl -s -o /dev/null -w "%{http_code}" \
    -X POST \
    -F "fileType=csv" \
    "$BASE_URL/users/upload")
if [ "$response" = "400" ]; then
    print_status 0 "Missing file properly rejected (HTTP $response)"
else
    print_status 1 "Missing file not properly rejected (HTTP $response)"
fi

# Test 15: Non-existent User
echo
print_info "Test 15: Get Non-existent User (Expected to fail)"
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/users/99999")
if [ "$response" = "404" ]; then
    print_status 0 "Non-existent user properly handled (HTTP $response)"
else
    print_status 1 "Non-existent user not properly handled (HTTP $response)"
fi

echo
echo "=========================================="
print_info "API Tests Completed!"
echo
print_info "To view detailed responses, remove '-s' flag from curl commands"
print_info "Example: curl '$BASE_URL/health' | jq"
echo
