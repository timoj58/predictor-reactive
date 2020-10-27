import boto3
import json

def lambda_handler(event, context):
    # TODO implement
    s3 = boto3.resource('s3')
    obj = s3.Object('predictor-client-data', 'upcoming-events/'+event['competition']+'/'+event['home']+'/'+event['away']+'/'+event['type'])
    file_content = obj.get()['Body'].read().decode('utf-8')
    json_content = json.loads(file_content)
    return {
        'statusCode': 200,
        'body': json_content
    }
