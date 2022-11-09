const apiUrl = 'https://localhost:4567'


function createSpace(name, owner) {
	let data = {name: name, owner: owner}

	fetch(apiUrl + '/spaces', {
		method: 'POST',
		credentials: 'include',
		body: JSON.stringify(data),
		headers: {
			'Content-Type': 'application/json'
		}
	})
	.then(response => {
		if (response.ok) {
			return response.json()
		}
		else {
			throw Error(response.statusText())
		}
	})
	.then(json => console.log('Created space: ', json.name, json.url))
	.catch(error => console.log('Error: ', error))
}

function processFormSubmit(e) {
	e.preventDefault();
	let spaceName = document.getElementById('spaceName').value;
	let owner = document.getElementById('owner').value;
	createSpace(spaceName, owner);
	return false;
}


window.addEventListener('load', function(e) {
	console.log('asdf')
	document.getElementById('createSpace')
	.addEventListener('submit', processFormSubmit);
});

