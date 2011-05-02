<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
    <head>
      Process Definitions Explorer
      <script type="text/javascript" src="/javascripts/demo/engineManagement.js"></script>
    </head>
    <body>
      <div id="engineStatus">
        <h1>Process Engine Status </h1>
        <table style="width:100%;">
          <thead>
            <tr>
             <th>ID</th>
             <th>Name</th>
             <th>Description</th>
            </tr>
          </thead>
          <tbody id="runningInstances">
          </tbody>
        </table>
      </div>
      <div id="definitionExplorer">
        <h1>Process Definition Explorer</h1>
        <table style="width:100%;">
          <thead>
            <tr>
              <th>Name</th>
              <th>Description</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody id="processDefinitionList"></tbody>
        </table>
		<h2>Demoprocess</h2>
		<img src="../../images/exampleProcess.png" />
      </div>
    </body>
</html>

