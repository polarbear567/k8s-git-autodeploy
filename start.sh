if [[ "${DEPLOY_IN_K8S:-true}" = "true" ]]; then
	export API_SERVER="https://$KUBERNETES_SERVICE_HOST:$KUBERNETES_SERVICE_PORT"
	export CA_CRT="/var/run/secrets/kubernetes.io/serviceaccount/ca.crt"
	export TOKEN="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)"
fi

java -jar k8s-git-autodeploy-1.0.0.jar &

tail -f /dev/null