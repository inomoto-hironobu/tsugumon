pipeline {
	agent any
	stages {
		stage ('test') {
			steps {
			    sh 'mvn clean test-compile test'
		    }
		}
	}
}