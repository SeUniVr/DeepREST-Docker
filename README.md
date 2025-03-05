# DeepREST (Docker)

Docker version of DeepREST, the REST API testing tool based on deep reinforcement learning.

---

## How to use DeepREST

### Provide an OpenAPI specification
To run DeepREST, you only need an OpenAPI specification for the REST API you wish to test. Please ensure that the OpenAPI specification is located in the `api/` directory and is named `openapi.json` (replace the current placeholder file).

> **Optional LLM Dictionary:** To enhance the performance of DeepREST, you have the option to provide a dictionary of LLM-generated input values for parameters. We can create this dictionary for you if you supply us with the OpenAPI specification. The LLM dictionary JSON file has to be places in `api/` with the name `llm-dictionary.json`.

### Build the Docker image
```shell
./build.sh
```

This script will delete any previous Docker image for DeepREST (if any), and build the image from scratch starting from the `Dockerfile` in this directory.

### Start the DeepREST container
```shell
./start.sh
```

This script will start the Docker container of DeepREST on the same network on the host machine to reach REST APIs in the host's network. The container will have access to the `api/` and `results/` directories via volume mappings to read the OpenAPI specification and write the results. The container is executed with the `--privileged` option to boost the performance of the Python component of DeepREST.

>Please remember to stop DeepREST. At the moment, we did not set a time budget and DeepREST runs indefinitely.

### Stop
```shell
./stop.sh
```

This script will stop (and remove) the Docker container of DeepREST.

### How to interpret results
Results will be stored in the `results/` folder as JSON reports. Each JSON report will summarize the test result. You can use your favourite tool to search for faulty executions among the generated reports.