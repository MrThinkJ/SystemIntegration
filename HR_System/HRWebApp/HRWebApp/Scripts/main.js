function checkNull(data, type, row){
  if (data == null){
    return ""
  } else return data
}

function connect(event) {
  var socket = new SockJS('http://127.0.0.1:8082/ws');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, onConnected, onError);
}

function onConnected() {
  stompClient.subscribe('/topic/mssql', onMessageReceived);
}

function onError(error) {
  connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
  connectingElement.style.color = 'red';
}

function onMessageReceived(payload) {
  let data = JSON.parse(payload.body);
  let counter = 0
    $('.mytable').DataTable({
    data: data,
    columns: [
        { data: null, "render": function (data, type, row) {
            const firstName = data.firstName
            const lastName = data.lastName
            return firstName + lastName
        }, },
        { data: 'city', render: checkNull },
        {data: 'email', render: checkNull},
        {data: 'phoneNumber', render: checkNull},
        {data: 'gender', render: checkNull},
        { data: 'shareholderStatus', render: checkNull },
        {
            'data': null, "render": function (data) {
                const firstName = data.firstName
                const lastName = data.lastName
                return `<div class='btn-group'> <button type='button' onclick=location.href='/Personals/Edit/${data.id}' class='btn btn-success'>Edit</button><button type='button' onclick=location.href='/Personals/Delete/${data.id}' class='btn btn-danger btn-delete'>Delete</button></div>`
            }
        }
      ],
      searching: true,
      "bDestroy": true
  });
}
connect()
axios.get('http://localhost:8082/api/sql')
    .then(function (response) {
        const data = response.data
        let counter = 0
        $('.mytable').DataTable({
            data: data,
            columns: [
                {
                    data: null, "render": function (data, type, row) {
                        const firstName = data.firstName
                        const lastName = data.lastName
                        return firstName + lastName
                    },
                },
                { data: 'city', render: checkNull },
                { data: 'email', render: checkNull },
                { data: 'phoneNumber', render: checkNull },
                { data: 'gender', render: checkNull },
                { data: 'shareholderStatus', render: checkNull },
                {
                    'data': null, "render": function (data) {
                        const firstName = data.firstName
                        const lastName = data.lastName
                        return `<div class='btn-group'> <button type='button' onclick=location.href='/Personals/Edit/${data.id}' class='btn btn-success'>Edit</button><button type='button' onclick=location.href='/Personals/Delete/${data.id}' class='btn btn-danger btn-delete'>Delete</button></div>`
                    }
                }
            ],
            searching: true,
            "bDestroy": true
        });
    })
