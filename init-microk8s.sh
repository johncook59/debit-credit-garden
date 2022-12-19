#!/bin/bash

microk8s enable dns ingress registry storage 
#microk8s enable metallb:192.168.64.250-192.168.64.254
#microk8s kubectl apply -f ingress-service.yaml
