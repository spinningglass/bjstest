# bjstest
#
# SkillTest.java will query a github organization for users who don't have a user name entered.
# If found, nameless users will be emailed if an email address is available.
# 
# To run SkillTest.java:
#
# download 2 jar files used with Codehaus Jackson
# Data Mapper - https://mvnrepository.com/artifact/org.codehaus.jackson/jackson-mapper-asl/1.9.13
# Jackson Core - https://mvnrepository.com/artifact/org.codehaus.jackson/jackson-core-asl/1.9.13
# 
# These jar files will need to be accessible to the Java library for SkillTest to function correctly.
#
# For the selected organization you wish to search, one owner user will need to be a member of the organization.
# That owner user will need a personal access token. 
#
# Enter the organization name, the owner login, and owner personal access token into the specified variables of the main
# method of SkillTest.java.
#
# Incomplete items:
# email the user
# send a list of emailed users to AWS for record keeping
