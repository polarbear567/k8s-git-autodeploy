# k8s-git-autodeploy
This tool can help you manage the yaml (JSON) files in Git and automatically deploy them to the k8s cluster, so as to realize file to version control, facilitate team management and avoid manual deployment.
## Premise
* Make sure that you can access the k8s cluster network
* you can operate the k8s cluster by serviceaccount in pod
## Installation and Getting Started
* Clone the code
* In the project root catalog, run ***mvn clean package***
* Run ***docker build -t [imageName]:[imageTag] .***

then you can run this image in your local or in k8s cluster.These methods are described below.
### K8S cluster(recommended)
* deploy this image to your k8s cluster as a deployment
* create a service for the deployment

### Local by docker (not recommended, just for testing)
* Run ***docker run -e [some envs] [imageName]:[imageTag]***
* Run ***docker exec -it [container id] bash***
* Configure the **config** file to **$HOME/.kube/**
* Test whether the kubectl command can obtain the k8s cluster information

### Local by java (not recommended, just for testing)
* Configure the **config** file to **$HOME/.kube/**
* Test whether the kubectl command can obtain the k8s cluster information
* Run ***java -jar [your jar]***

## Two modes
1. Git webhook: through the web hook function of git, the modification request is sent to the specified URL.
2. Timing git pull: set the pull interval to realize automatic deployment

## Env Sets

|  Name   | Required  | Settable Value | Mode | Description |
|  ----  | ----  | ----  | ----  | ----  |
| REMOTE_REPO_URL  | Y |  |  | repo http address  |
| BRANCH  | Y |  |   | your branch name(master is recommended)  |
| LOCAL_PATH  | Y |   |   | git file path in your local or pod  |
| USERNAME  | Y |   |   | repo username  |
| PASSWORD  | Y |   |   | repo password  |
| AUTODEPLOY_TYPE  | Y | webhook/timing  |   | use mode  |
| TIME_INTERVAL_OF_PULL  | N |   | timing  | time interval of pull |
| NEED_PROXY  | N |   |   | if need proxy  |
| HTTP_PROXY  | N |   |   | proxy address  |
| HTTP_PORT  | N |   |   | proxy port  |

## Attention
For git modification, only add, modify and delete are supported now, because I consider that rename and copy are not very necessary for managing files.

## Todo
* add deployment.yaml and service.yaml
* retry
