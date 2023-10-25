import boto3
import json
s3 = boto3.resource('s3')

def lambda_handler(event, context):
    # TODO implement
    obj = s3.Object('predictor-client-data', 'previous-fixtures/'+event['competition'])
    file_content = obj.get()['Body'].read().decode('utf-8')
    json_content = json.loads(file_content)
    return {
        'statusCode': 200,
        'body': json_content
    }
