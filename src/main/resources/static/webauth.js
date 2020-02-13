import { solveRegistrationChallenge, solveLoginChallenge } from '@webauthn/client';

document.addEventListener('DOMContentLoaded', (event) => {
    document.getElementById("securityKeyActivate").addEventListener("click", securityKeyCallback);
    document.getElementById('securityKeyActivate').style.display='block';
})

async function securityKeyCallback() {
    const challenge = await fetch('https://localhost:8000/login', {
        method: 'POST',
        headers: {
            'content-type': 'Application/Json'
        },
        body: JSON.stringify({ email: 'test@test' })
    })
        .then(response => response.json());


    const credentials = await solveLoginChallenge(challenge);
    const { loggedIn } = await fetch(
        'https://localhost:8000/login-challenge',
        {
            method: 'POST',
            headers: {
                'content-type': 'Application/Json'
            },
            body: JSON.stringify(credentials)
        }
    ).then(response => response.json());

    if (loggedIn) {
        displayMessage('You are logged in');
        return;
    }
    displayMessage('Invalid credential');
}