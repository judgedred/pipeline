# pipeline

<h3>REST API</h3>
<ul>
<li>POST /pipeline/create -- Create pipeline according to the provided config </li>
<li>PUT /pipeline/update -- Update pipeline according to the provided config </li>
<li>GET /pipeline/read/{pipelineName} -- Get pipeline </li>
<li>DELETE /pipeline/delete/{pipelineName} -- Delete pipeline </li>
<li>POST /pipeline/execute/{pipelineName} -- Execute pipeline </li>
<li>GET /pipeline/show/{executionId} -- Get execution status </li>
<li>GET /pipeline/stop/{executionId} -- Stop execution </li>
</ul>
Test config is in config/pipeline.yml.

