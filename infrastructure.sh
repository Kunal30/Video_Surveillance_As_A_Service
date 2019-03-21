vs_vpc_name="vs_vpc"
vs_security_group_name="vs_security_group"
vs_key_pair_name="vs_key_pair"
vs_instance_profile_name="vs_instance_profile"
vs_role_name="vs_role"

vs_input_queue_name="vs_input_queue"
vs_output_queue_name="vs_output_queue"

vs_s3_bucket_name="vs-result-bucket-adi" # should be a DNS compliant name

vs_web_instance_name="web_instance"
vs_web_image_id="ami-06397100adf427136"
vs_web_instance_type="t2.micro"
vs_web_instance_count=1

vs_app_instance_name="app_instance_1"
vs_app_image_id="ami-0e355297545de2f82"
vs_app_instance_type="t2.micro"
vs_app_instance_count=1

vs_app_instance_ami_name="app_instance_ami"

vs_vpc_cidr_block="10.0.0.0/16"
vs_subnet_cidr_block="10.0.0.0/24"


if [ "$1" == "create" ]; then
	echo 'BUILDING UP THE INFRASTRUCTURE'

	################################
	########## S3 SETTING ##########
	################################

	# code for non available s3 bucket?

	echo 'creating S3 bucket...'
	aws_region=`aws configure get region`
	s3_bucket_url=`aws s3api create-bucket --bucket $vs_s3_bucket_name --region $aws_region --create-bucket-configuration LocationConstraint=$aws_region --query 'Location' --output text`
	echo "vs_s3_bucket_url=$s3_bucket_url" >> aws-resources.properties

	################################
	########## SQS SETTING #########
	################################

	echo 'creating SQS input queue...'
	input_queue_url=`aws sqs create-queue --queue-name $vs_input_queue_name --query 'QueueUrl' --output text`
	echo "vs_input_queue_url=$input_queue_url" >> aws-resources.properties

	echo 'creating SQS output queue...'
	output_queue_url=`aws sqs create-queue --queue-name $vs_output_queue_name --query 'QueueUrl' --output text`
	echo "vs_output_queue_url=$output_queue_url" >> aws-resources.properties

	################################
	########## VPC SETTING #########
	################################

	echo 'creating VPC...'
	vpc_id=`aws ec2 create-vpc --cidr-block $vs_vpc_cidr_block --query 'Vpc.VpcId' --output text`
	aws ec2 create-tags --resources $vpc_id --tags "Key=\"Name\",Value=\"$vs_vpc_name\""

	echo 'enabling DNS for VPC...'
	aws ec2 modify-vpc-attribute --vpc-id $vpc_id --enable-dns-support "{\"Value\":true}"
	aws ec2 modify-vpc-attribute --vpc-id $vpc_id --enable-dns-hostnames "{\"Value\":true}"

	echo 'creating internet gateway...'
	internet_gateway_id=`aws ec2 create-internet-gateway --query 'InternetGateway.InternetGatewayId' --output text`

	echo 'attaching internet gateway to VPC...'
	aws ec2 attach-internet-gateway --internet-gateway-id $internet_gateway_id --vpc-id $vpc_id

	echo 'creating subnet...'
	subnet_id=`aws ec2 create-subnet --vpc-id $vpc_id --cidr-block $vs_subnet_cidr_block --query 'Subnet.SubnetId' --output text`

	echo 'creating routes...'
	route_table_id=`aws ec2 describe-route-tables --filters Name=vpc-id,Values=$vpc_id --query 'RouteTables[0].RouteTableId' --output text`
	aws ec2 create-route --route-table-id $route_table_id --destination-cidr-block 0.0.0.0/0 --gateway-id $internet_gateway_id > /dev/null

	echo 'creating security group...'
	security_group_id=`aws ec2 create-security-group --group-name $vs_security_group_name --description $vs_security_group_name --vpc-id $vpc_id --query 'GroupId' --output text`

	echo 'allowing incoming traffic...'
	aws ec2 authorize-security-group-ingress --group-id $security_group_id --protocol tcp --port 22 --cidr 0.0.0.0/0
	aws ec2 authorize-security-group-ingress --group-id $security_group_id --protocol tcp --port 80 --cidr 0.0.0.0/0
	aws ec2 authorize-security-group-ingress --group-id $security_group_id --protocol tcp --port 443 --cidr 0.0.0.0/0
	aws ec2 authorize-security-group-ingress --group-id $security_group_id --protocol tcp --port 8080 --cidr 0.0.0.0/0

	echo 'creating key pair...'
	# aws ec2 create-key-pair --key-name $vs_key_pair_name > /dev/null
	aws ec2 create-key-pair --key-name $vs_key_pair_name --query 'KeyMaterial' --output text > ~/.ssh/$vs_key_pair_name.pem
	chmod 400 ~/.ssh/$vs_key_pair_name.pem

	################################
	########## IAM SETTING #########
	################################

	echo 'creating instance profile...'
	aws iam create-instance-profile --instance-profile-name $vs_instance_profile_name > /dev/null

	echo 'creating role...'
	aws iam create-role --role-name $vs_role_name --assume-role-policy-document "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"Service\":[\"ec2.amazonaws.com\"]},\"Action\":[\"sts:AssumeRole\"]}]}" > /dev/null

	echo 'attaching policies to role...'
	aws iam attach-role-policy --role-name $vs_role_name --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess
	aws iam attach-role-policy --role-name $vs_role_name --policy-arn arn:aws:iam::aws:policy/AmazonSQSFullAccess

	echo 'attaching role to instance profile...'
	aws iam add-role-to-instance-profile --instance-profile-name $vs_instance_profile_name --role-name $vs_role_name

	sleep 10 # to avoid race condition of using run-instances just after creating instance profile

	################################
	########## EC2 SETTING #########
	################################

	echo 'creating web instance...'
	web_instance_id=`aws ec2 run-instances --iam-instance-profile Name=$vs_instance_profile_name --image-id $vs_web_image_id --count $vs_web_instance_count --instance-type $vs_web_instance_type --key-name $vs_key_pair_name --security-group-ids $security_group_id --subnet-id $subnet_id --associate-public-ip-address --query "Instances[0].InstanceId" --output text`
	aws ec2 create-tags --resources $web_instance_id --tags "Key=\"Name\",Value=\"$vs_web_instance_name\""

	echo 'creating first app instance...'
	app_instance_id=`aws ec2 run-instances --iam-instance-profile Name=$vs_instance_profile_name --image-id $vs_app_image_id --count $vs_app_instance_count --instance-type $vs_app_instance_type --key-name $vs_key_pair_name --security-group-ids $security_group_id --subnet-id $subnet_id --associate-public-ip-address  --query "Instances[0].InstanceId" --output text`
	aws ec2 create-tags --resources $app_instance_id --tags "Key=\"Name\",Value=\"$vs_app_instance_name\""

	sleep 60 # for proper creation of ec2 instances

	################################
	########## DEPLOYMENT ##########
	################################

	web_instance_ip=`aws ec2 describe-instances --filters Name=tag:Name,Values=$vs_web_instance_name Name=vpc-id,Values=$vpc_id --query 'Reservations[0].Instances[0].PublicIpAddress' --output text`

	echo "deploying web tier application..."
	scp -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no install_java.sh ubuntu@$web_instance_ip:~/
	scp -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no WebTier-1.0.0.jar ubuntu@$web_instance_ip:~/
	ssh -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no -t ubuntu@$web_instance_ip bash install_java.sh > /dev/null
	# ssh -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no -t ubuntu@$web_instance_ip java -jar WebTier-1.0.0.jar &
	ssh -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no -t ubuntu@$web_instance_ip java -jar WebTier-1.0.0.jar
	echo "web tier application deployed!"

	app_instance_ip=`aws ec2 describe-instances --filters Name=tag:Name,Values=$vs_app_instance_name Name=vpc-id,Values=$vpc_id --query 'Reservations[0].Instances[0].PublicIpAddress' --output text`

	echo "deploying app tier application..."
	scp -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no install_java.sh ubuntu@$app_instance_ip:~/
	scp -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no darknet_test.py ubuntu@$app_instance_ip:~/darknet/
	scp -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no yolov3-tiny.weights ubuntu@$app_instance_ip:~/darknet/
	# scp -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no deeplearning.sh ubuntu@$app_instance_ip:~/darknet/
	scp -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no AppTier-1.0.0.jar ubuntu@$app_instance_ip:~/darknet/
	ssh -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no -t ubuntu@$app_instance_ip bash install_java.sh > /dev/null
	# ssh -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no -t ubuntu@$app_instance_ip java -jar ./darknet/AppTier-1.0.0.jar &
	ssh -i ~/.ssh/$vs_key_pair_name.pem -o StrictHostKeyChecking=no -t ubuntu@$app_instance_ip java -jar ./darknet/AppTier-1.0.0.jar
	echo "app tier application deployed!"

	sleep 60 # for proper deployment of applications

	################################
	########## APP IMAGE ###########
	################################

	# echo "creating app instance AMI image..."
	# app_instance_ami_id=`aws ec2 create-image --instance-id $app_instance_id --name "app_instance_ami" --no-reboot --query 'ImageId' --output text`
	# aws ec2 create-tags --resources $app_instance_ami_id --tags "Key=\"Name\",Value=\"$vs_app_instance_ami_name\""


elif [ "$1" == "destroy" ]; then
	echo 'BREAKING DOWN THE INFRASTRUCTURE'

	echo 'deleting S3 bucket...'
	aws_region=`aws configure get region`
	aws s3api delete-bucket --bucket $vs_s3_bucket_name --region $aws_region

	echo 'deleting SQS input queue...'
	input_queue_url=`aws sqs get-queue-url --queue-name $vs_input_queue_name --query 'QueueUrl' --output text`
	aws sqs delete-queue --queue-url $input_queue_url

	echo 'deleting SQS output queue...'
	output_queue_url=`aws sqs get-queue-url --queue-name $vs_output_queue_name --query 'QueueUrl' --output text`
	aws sqs delete-queue --queue-url $output_queue_url

	# echo 'deleting app instance AMI image...'
	# app_instance_ami_id=`aws ec2 describe-images --filters Name=tag:Name,Values=$vs_app_instance_ami_name --query 'Images[0].ImageId' --output text`
	# aws ec2 describe-images --filters Name=tag:Name,Values=$app_instance_ami_id
	# aws ec2 deregister-image --image-id app_instance_ami_id

	# # DELETE SNAPSHOT REMAINING


	vpc_id=`aws ec2 describe-vpcs --filters Name=tag:Name,Values=$vs_vpc_name  --query 'Vpcs[0].VpcId' --output text`

	echo 'deleting instances...'
	aws ec2 terminate-instances --instance-ids $(aws ec2 describe-instances --filters  Name=vpc-id,Values=$vpc_id --query "Reservations[].Instances[].[InstanceId]" --output text | tr '\n' ' ') > /dev/null

	sleep 60 # enough time for instances to be marked as shutdown

	echo 'detaching role from instance profile...'
	aws iam remove-role-from-instance-profile --instance-profile-name $vs_instance_profile_name --role-name $vs_role_name
	
	echo 'detaching policies from role...'
	aws iam detach-role-policy --role-name $vs_role_name --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess
	aws iam detach-role-policy --role-name $vs_role_name --policy-arn arn:aws:iam::aws:policy/AmazonSQSFullAccess

	echo 'deleting role...'
	aws iam delete-role --role-name $vs_role_name

	echo 'deleting instance profile...'
	aws iam delete-instance-profile --instance-profile-name $vs_instance_profile_name

	echo 'deleting key pair...'
	aws ec2 delete-key-pair --key-name $vs_key_pair_name
	rm -f ~/.ssh/$vs_key_pair_name.pem

	echo 'deleting security group...'
	security_group_id=`aws ec2 describe-security-groups --filter Name=group-name,Values=$vs_security_group_name --query 'SecurityGroups[0].GroupId' --output text`
	aws ec2 delete-security-group --group-id $security_group_id
	
	echo 'deleting routes...'
	route_table_id=`aws ec2 describe-route-tables --filters Name=vpc-id,Values=$vpc_id --query 'RouteTables[0].RouteTableId' --output text`
	aws ec2 delete-route --route-table-id $route_table_id --destination-cidr-block 0.0.0.0/0

	echo 'deleting subnet...'
	subnet_id=`aws ec2 describe-subnets --filters Name=vpc-id,Values=$vpc_id --query 'Subnets[0].SubnetId' --output text`
	aws ec2 delete-subnet --subnet-id $subnet_id 

	echo 'detaching internet gateway from VPC...'
	internet_gateway_id=`aws ec2 describe-internet-gateways --filters Name=attachment.vpc-id,Values=$vpc_id --query 'InternetGateways[0].InternetGatewayId' --output text`
	aws ec2 detach-internet-gateway --internet-gateway-id $internet_gateway_id --vpc-id $vpc_id

	echo 'deleting internet gateway...'
	aws ec2 delete-internet-gateway --internet-gateway-id $internet_gateway_id

	echo 'deleting VPC...'
	aws ec2 delete-vpc --vpc-id $vpc_id

else
	echo "use either \"create\" argument to create infrastructure or \"destroy\" argument to destroy infrastructure"
fi