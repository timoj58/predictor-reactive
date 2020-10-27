import boto3
import json

def lambda_handler(event, context):
    # TODO implement
    s3 = boto3.resource('s3')
    bucket = s3.Bucket('predictor-client-data')

    json_content = []

    for object_summary in bucket.objects.filter(Prefix='previous-events/'+event['competition']+'/'+event['team']+'/'+event['type']):
        obj = s3.Object('predictor-client-data', object_summary.key)
        file_content = obj.get()['Body'].read().decode('utf-8')
        json_content.append(json.loads(file_content))

    response = {
        "data": json_content,
        "team": event['team'],
        "type": event['type']
    }

    return {
        'statusCode': 200,
        'body': response
    }
