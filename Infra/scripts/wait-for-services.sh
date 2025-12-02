#!/bin/bash

echo "=== Waiting for Services to Start ==="
echo ""
echo "Keycloak: http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com"
echo "Operate:  http://camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com/operate"
echo ""
echo "This will check every 30 seconds until services are ready..."
echo "Press Ctrl+C to stop"
echo ""

ALB_DNS="camunda-keycloak-alb-1328018596.us-east-1.elb.amazonaws.com"

check_service() {
    local url=$1
    local name=$2
    
    status=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "$url" 2>/dev/null)
    
    if [ "$status" = "200" ] || [ "$status" = "302" ] || [ "$status" = "303" ]; then
        echo "✅ $name is ready! (HTTP $status)"
        return 0
    else
        echo "⏳ $name not ready yet (HTTP $status)"
        return 1
    fi
}

keycloak_ready=false
operate_ready=false

while true; do
    echo "--- $(date +%H:%M:%S) ---"
    
    if [ "$keycloak_ready" = false ]; then
        if check_service "http://$ALB_DNS" "Keycloak"; then
            keycloak_ready=true
        fi
    else
        echo "✅ Keycloak is ready!"
    fi
    
    if [ "$operate_ready" = false ]; then
        if check_service "http://$ALB_DNS/operate" "Camunda Operate"; then
            operate_ready=true
        fi
    else
        echo "✅ Camunda Operate is ready!"
    fi
    
    if [ "$keycloak_ready" = true ] && [ "$operate_ready" = true ]; then
        echo ""
        echo "========================================="
        echo "🎉 All services are ready!"
        echo "========================================="
        echo ""
        echo "Keycloak:  http://$ALB_DNS"
        echo "           Username: admin / Password: admin123"
        echo ""
        echo "Operate:   http://$ALB_DNS/operate"
        echo "           Username: demo / Password: demo"
        echo ""
        echo "Tasklist:  http://$ALB_DNS/tasklist"
        echo "           Username: demo / Password: demo"
        echo ""
        break
    fi
    
    echo ""
    sleep 30
done
