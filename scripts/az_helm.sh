#!/bin/bash

source ./scripts/functions.sh

OVERRIDDEN_VALUES=""
NAMESPACE=default

for i in "$@"; do
    case ${i} in
    -au=* | --azure-user=*)
        AZ_USER="${i#*=}"
        shift
        ;;
    -ap=* | --azure-password=*)
        AZ_PASSWORD="${i#*=}"
        shift
        ;;
    -at=* | --azure-tenant=*)
        AZ_TENANT="${i#*=}"
        shift
        ;;
    -rg=* | --resource-group=*)
        RESOURCE_GROUP="${i#*=}"
        shift
        ;;
    -cn=* | --cluster-name=*)
        CLUSTER_NAME="${i#*=}"
        shift
        ;;
    -pn=* | --project-name=*)
        PROJECT_NAME="${i#*=}"
        shift
        ;;
    -n=* | --namespace=*)
        NAMESPACE="${i#*=}"
        shift
        ;;
    -e=* | --env=*)
        ENV="${i#*=}"
        shift
        ;;
    -v=* | --version=*)
        VERSION_TOKEN="${i#*=}"
        if [[ -z ${VERSION} ]]; then
            VERSION_TOKEN=$(sed "s~/~-~g" <<<${VERSION_TOKEN})
            if [[ ! -z ${VERSION_TOKEN} ]]; then                
                VERSION=${VERSION_TOKEN}
                OVERRIDDEN_VALUES="${OVERRIDDEN_VALUES} --set version=${VERSION}"
                echo "Version set to ${VERSION_TOKEN}"
            fi
        else
            echo "Omitting version ${VERSION_TOKEN}, version already defined with ${VERSION}"
        fi
        shift
        ;;
    -h | --help)
        echo "Handled parameters are :"
        echo "-au, --azure-user     : sets the azure user"
        echo "-ap, --azure-password : sets the azure password"
        echo "-at, --azure-tenant   : sets the azure tenant"
        echo "-rg, --resource-group : sets the resource group"
        echo "-cn, --cluster-name   : sets the cluster name"
        echo "-pn, --project-name   : sets the project name (look for templates folder with the same name)"
        echo "-n, --namespace       : sets the namespace in which containers will be deployed"
        echo "-e, --env             : selects the [env].json values file as base file"
        echo "-v, --version         : sets the docker image version to deploy on the cluster"
        echo "[KEY]=[VALUE]         : exports environment variables"
        ;;
    *)
        KEY=${i:0:`expr index "$i" '='`-1}
        VALUE=${i:`expr index "$i" '='`}

        if [[ "$VALUE" ]]; then
            # escape commas
            VALUE="$(sed 's/'\,'/\\&/g' <<<"$VALUE")"

            echo "Overriding ${KEY} with value ${VALUE}"
            OVERRIDDEN_VALUES="${OVERRIDDEN_VALUES} --set ${KEY}=${VALUE}"
        fi
        ;;
    esac
done

requiredParameter "${AZ_USER}" "--azure-user"
requiredParameter "${AZ_PASSWORD}" "--azure-password"
requiredParameter "${AZ_TENANT}" "--azure-tenant"
requiredParameter "${RESOURCE_GROUP}" "--resource-group"
requiredParameter "${CLUSTER_NAME}" "--cluster-name"
requiredParameter "${PROJECT_NAME}" "--project-name"
requiredParameter "${ENV}" "--env"

az login --service-principal -u ${AZ_USER} -p ${AZ_PASSWORD} --tenant ${AZ_TENANT}
az aks get-credentials --resource-group ${RESOURCE_GROUP} --name ${CLUSTER_NAME}
helm init --service-account tiller --client-only

echo helm upgrade \
    --install --debug \
    --values ./k8s/${ENV}.yml \
    ${OVERRIDDEN_VALUES} \
    --namespace=${NAMESPACE} \
    --force "${PROJECT_NAME}" \
    ./k8s/${PROJECT_NAME}

helm upgrade "${PROJECT_NAME}" ./k8s/${PROJECT_NAME} --install --values ./k8s/${ENV}.yml ${OVERRIDDEN_VALUES} --namespace=${NAMESPACE} --force
