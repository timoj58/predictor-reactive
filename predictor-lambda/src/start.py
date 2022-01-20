import boto3

region = 'us-east-1'
instances = ['i-004731dd0f9fb3f2d',
             'i-09d8635357f404d3d',
             'i-01983d60f118fe607',
             'i-0d12ab28420637083',
             'i-09ded665af748bae5',
             'i-074d858a82de6b5b5',
             'i-03a283f90d3cdd057',
             'i-0217e0c198d5e62a1',
             'i-08acf9468aaae5414',
             'i-039c5198ecc2ec72f',
             'i-0f084ef6f13fc4f05']

ec2 = boto3.client('ec2', region_name=region)


def lambda_handler(event, context):
    ec2.start_instances(InstanceIds=instances)
    print('started your instances: ' + str(instances))
