import boto3
import json

s3 = boto3.resource('s3')


def lambda_handler(event, context):
    bucket = s3.Bucket('predictor-client-data')

    json_content = []

    for object_summary in bucket.objects.filter(Prefix='player-events/' + event['competition']):
        obj = s3.Object('predictor-client-data', object_summary.key)
        file_content = obj.get()['Body'].read().decode('utf-8')
        json_content.append(json.loads(file_content))
        match = filter(lambda result: result['home'] == event['home'] and result['away'] == event['away'], json_content)

    response = {
        "data": list(match)
    }

    return {
        'statusCode': 200,
        'body': response
    }
