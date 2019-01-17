function errorAlert(d) {
	
}

var app;
Vue.config.devtools = true;
Vue.component('enquete', {
	props: ['enq'],
	template: '<div class="enquete" v-on:click="selectEnquete(enq.id)">'
		+'<p v-bind:eid="enq.id"><a v-bind:href="\'enquete?id=\'+enq.id">id:{{enq.id}}</a> 作成日:{{enq.created}}</p>'
		+'<p>{{enq.description}}</p>'
		+'<ol>'
		+'<li v-for="e in enq.entries">{{e.string}}</li>'
		+'</ol>'
		+'<p>{{enq.total}}</p>'
		+'</div>',
	methods: {
		selectEnquete: function(d) {
			app.targetEnqueteId = d;
			app.callEnquete();
		}
	}
});

window.addEventListener("load", function() {
	app = new Vue({
		el : "#app",
		data : {
			ownEnquete: null,
			dealtEnquete: null,
			answers: null,
			enquetes : null,
			user : {},
			targetEnqueteId:null,
			targetEnquete : null,
			select : null,
			entry:null,
			enquete: {
				description:null,
				entries:[]
			},
			search : {
				keyword : null,
				enquetes: null,
				page: 0
			},
			ranking : {
				enquetes: null,
				page: 0,
			}
		},
		mounted: function () {
			$.ajax({
				url : "api/user",
				success : function(d) {
					console.log(d);
					app.user = d;
				},
				error : function(d) {
					console.warn(d);
				}
			});
			$.ajax({
				url : "api/dealtEnquete",
				success : function(d) {
					console.log(d);
					app.dealtEnquete = d;
				},
				error : function(d) {
					console.warn(d);
				}
			});
			$.ajax({
				url : "api/home",
				success : function(d) {
					console.log(d);
					if (d.hasOwnProperty('ownEnquete')) {
						app.ownEnquete = d.ownEnquete;
					}
					app.answers = d.answers;
				},
				error : function(d) {
					console.warn(d);
				}
			});
		},
		computed:{
			format:function(utc) {
				return moment(utc).format();
			},
			selected: function() {
				return entry.number = this.entry;
			},
		},
		methods : {
			
			getUserInfo: function() {
				jQuery.ajax({
					type: "get",
					url: "api/user",
					success: function(data) {
						
					},
					error: function(data) {
						
					}
				});
			},
			deleteEnquete: function() {
				console.log();
				jQuery.ajax({
					type: "delete",
					url: "api/enquete",
					success: function(data) {
						console.log(data);
					},
					error: function(d) {
						console.warn(d);
						alert(d);
					}
				});
			},
			deleteAnswer : function(eid) {
				console.log(eid);
				if(!this.user.access) {
					jQuery.ajax({
						type: "delete",
						url: "api/answer/" + eid,
						success: function(data) {
							$("#enquete"+eid).remove();
							console.log(data);
						},
						error: function(d) {
							console.warn(d);
							alert(d);
						}
					});
				}
			},
			sendAnswer : function(enqueteId) {
				console.log(enqueteId + "" + this.entry);
				var checked = this.entry !== null;
				if(checked) {
					let entry = Number(checked) + 1;
					$.ajax({
						url : "api/answer/" + enqueteId + "/" + app.entry,
						type : "put",
						success : function(d) {
							console.log(d);
						},
						error : function(d) {
							console.log(d);
							alert(d.message);
						}
					});
				}
				
			},
			sendEnquete : function(d) {
				console.log(d);
				$.ajax({
					url : "api/enquete",
					type : "post",
					contentType : "application/json",
					data : JSON.stringify(this.enquete),
					success : function(d) {
						console.log(d);
						app.user.access = true;
						app.user.ownEnquete = this.enquete;
					},
					error : function(d) {
						console.warn(d);
						app.user.access = true;
						alert(d.message);
					}
				});
			},
			
			plusEntry: function() {
				this.enquete.entries.push({string:null,number:this.enquete.entries.length + 1});
			},
			minusEntry: function() {
				this.enquete.entries = this.enquete.entries.slice(0, this.enquete.entries.length - 1);
			},
			callEnquete : function() {
				console.log("call");
				if(this.targetEnqueteId != null) {
					$.ajax({
						url : "api/enquete/" + app.targetEnqueteId,
						success : function(d) {
							console.log(d);
							app.targetEnquete = d;
							for(var i = 0; i<app.answers.length;i++) {
								if(app.answers[i].enquete.id === d.id) {
									app.entry = app.answers[i].entry;
								}
							}
						}
					});
				}
			},
			dosearch:function(page) {
				this.search.page = page;
				$.ajax({
					url : "api/search/" + app.search.keyword + "/" + app.search.page,
					success : function(d) {
						app.search.enquetes = d;
					},
					error : function(d) {
						console.warn(d);
					}
				});
			},
			getRanking: function(page) {
				this.ranking.page = page;
				console.log(page);
				$.ajax({
					url: "api/ranking/"+page,
					success: function(d) {
						console.log(d);
						if(d) {
							app.ranking.enquetes = d;
						}
					}
				});
			}
		}
	});
	
	var query = location.search.substr(1)
	var params = query.slice('&');
	for(var param of params) {
		if(param.startsWith("enqueteId")) {
			alert(location.hash);
		}
	}
});