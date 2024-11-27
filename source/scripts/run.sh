# Print welcome message.
echo "Welcome to DeepREST. The tool is starting."

# Copy OpenAPI specification and LLM dictionary to the RestTestGen's API folder
cp /api/openapi.json /source/resttestgen-framework/apis/api-under-test/specifications/openapi.json
cp /api/llm-dictionary.json /source/resttestgen-framework/apis/api-under-test/dictionaries/llm.json

# Customize host of the API under test (currently disabled)
#echo "host: http://localhost:$PORT" >> /tool/apis/experiment-api/api-config.yml

# Launch DeepREST Python and RestTestGen in Java
cd /source/deeprest-py && python3 deeprest.py & \
cd /source/resttestgen-framework && ./gradlew run --no-daemon --warn