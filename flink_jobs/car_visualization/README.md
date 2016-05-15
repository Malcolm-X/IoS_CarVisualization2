In order to run the project you need to use eclipse and add the following lines to {project directoy}/.settings/org.eclipse.jdt.core.prefs:

`org.eclipse.jdt.core.compiler.codegen.lambda.genericSignature=generate`

In addition check the that the following attributes are set to the listed values:

`org.eclipse.jdt.core.compiler.codegen.targetPlatform=1.8
org.eclipse.jdt.core.compiler.compliance=1.8
org.eclipse.jdt.core.compiler.source=1.8`

For more information check [this](https://ci.apache.org/projects/flink/flink-docs-release-1.0/apis/java8.html) website.
