import requests 
import json

BASE_URL = 'http://scavengr.meteor.com/hunts/'
hunts = {}
huntsid = [] 
def query_hunts(resource_url):
	url = '{0}{1}'.format(BASE_URL, resource_url)
	response = requests.get(url)
	if response.status_code == 200:
		for item in json.loads(response.text):
			huntsid.append(item['id'])


	return None

def query_points(resource_url):
	url = '{0}{1}'.format(BASE_URL, resource_url)
	response = requests.get(url)
	if response.status_code == 200:
		for item in json.loads(response.text)['tasks']:
			print "{}, {}, {}".format(item['answer'], item['latitude'], item['longitude'])
			
	return None

query_hunts("")
# print huntsid
for item in huntsid:
	query_points(item);

