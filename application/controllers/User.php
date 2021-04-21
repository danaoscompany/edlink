<?php

include 'Util.php';

class User extends CI_Controller {
	
	public function login() {
		$email = $this->input->post('email');
		$password = $this->input->post('password');
		$users = $this->db->query("SELECT * FROM `users` WHERE `email`='" . $email . "' AND `password`='" . $password . "'")->result_array();
		if (sizeof($users) > 0) {
			$user = $users[0];
			$user['response_code'] = 1;
			echo json_encode($user);
		} else {
			echo json_encode(array(
				'response_code' => -1
			));
		}
	}
	
	public function signup() {
		$email = $this->input->post('email');
		$password = $this->input->post('password');
		$name = $this->input->post('name');
		$users = $this->db->query("SELECT * FROM `users` WHERE `email`='" . $email . "'")->result_array();
		if (sizeof($users) > 0) {
			echo json_encode(array(
				'response_code' => -1
			));
		} else {
			$this->db->insert('users', array(
				'email' => $email,
				'password' => $password,
				'name' => $name
			));
			$userID = $this->db->insert_id();
			$user = $this->db->query("SELECT * FROM `users` WHERE `id`=" . $userID)->row_array();
			$user['response_code'] = 1;
			$code = "TEST";
			$user['verification_code'] = $code;
			echo json_encode($user);
			Util::sendEmail($email, "Kode verifikasi Anda adalah: " . $code, "Hi " . $name . ", pendaftaran Anda berhasil, tinggal 1 langkah lagi. Kode verifikasi Anda adalah: <b>" . $code . "</b>.");
		}
	}
	
	public function verify_email() {
		$email = $this->input->post('email');
		$this->db->query("UPDATE `users` SET `email_verified`=1 WHERE `email`='" . $email . "'");
	}
	
	public function send_verification_code() {
		$email = $this->input->post('email');
		$users = $this->db->query("SELECT * FROM `users` WHERE `email`='" . $email . "'")->result_array();
		$code = "TEST";
		Util::sendEmail($email, "Kode verifikasi Anda adalah: " . $code, "Hi " . $users[0]['name'] . ", pendaftaran Anda berhasil, tinggal 1 langkah lagi. Kode verifikasi Anda adalah: <b>" . $code . "</b>.");
		echo json_encode(array(
			'verification_code' => $code
		));
	}
	
	public function get_posts() {
		$userID = intval($this->input->post('user_id'));
		$posts = $this->db->query("SELECT * FROM `posts` ORDER BY `date`")->result_array();
		for ($i=0; $i<sizeof($posts); $i++) {
			$posts[$i]['like_count'] = $this->db->query("SELECT * FROM `post_likes` WHERE `post_id`=" . $posts[$i]['id'])->num_rows();
			$posts[$i]['comment_count'] = $this->db->query("SELECT * FROM `post_comments` WHERE `post_id`=" . $posts[$i]['id'])->num_rows();
			$posts[$i]['is_liked'] = $this->db->query("SELECT * FROM `post_likes` WHERE `post_id`=" . $posts[$i]['id'] . " AND `user_id`=" . $userID)->num_rows()>0?1:0;
		}
		echo json_encode($posts);
	}
	
	public function get_comments() {
		$postID = intval($this->input->post('post_id'));
		$comments = $this->db->query("SELECT * FROM `post_comments` WHERE `post_id`=" . $postID)->result_array();
		for ($i=0; $i<sizeof($comments); $i++) {
			$comments[$i]['user'] = $this->db->query("SELECT * FROM `users` WHERE `id`=" . $comments[$i]['user_id'])->row_array();
		}
		echo json_encode($comments);
	}
	
	public function send_comment() {
		$userID = intval($this->input->post('user_id'));
		$postID = intval($this->input->post('post_id'));
		$comment = $this->input->post('comment');
		$date = $this->input->post('date');
		$this->db->insert('post_comments', array(
			'user_id' => $userID,
			'post_id' => $postID,
			'comment' => $comment,
			'date' => $date,
			'type' => 'text'
		));
		$commentID = $this->db->insert_id();
		$comment = $this->db->query("SELECT * FROM `post_comments` WHERE `id`=" . $commentID)->row_array();
		$comment['user'] = $this->db->query("SELECT * FROM `users` WHERE `id`=" . $userID)->row_array();
		echo json_encode($comment);
	}
	
	public function send_comment_image() {
		$userID = intval($this->input->post('user_id'));
		$postID = intval($this->input->post('post_id'));
		$date = $this->input->post('date');
		$config['upload_path']          = './userdata/';
        $config['allowed_types']        = '*';
        $config['max_size']             = 2147483647;
        $config['file_name']            = Util::generateUUIDv4();
        $this->load->library('upload', $config);
        if ($this->upload->do_upload('file')) {
			$this->db->insert('post_comments', array(
				'user_id' => $userID,
				'post_id' => $postID,
				'comment' => '',
				'image' => $this->upload->data()['file_name'],
				'date' => $date,
				'type' => 'image'
			));
			$commentID = $this->db->insert_id();
			$comment = $this->db->query("SELECT * FROM `post_comments` WHERE `id`=" . $commentID)->row_array();
			$comment['user'] = $this->db->query("SELECT * FROM `users` WHERE `id`=" . $userID)->row_array();
			echo json_encode($comment);
        } else {
			echo json_encode($this->upload->display_errors());
        }
	}
	
	public function update_post_like() {
		$userID = intval($this->input->post('user_id'));
		$postID = intval($this->input->post('post_id'));
		$liked = intval($this->input->post('liked'));
		$likeCount = $this->db->query("SELECT * FROM `post_likes` WHERE `user_id`=" . $userID . " AND `post_id`=" . $postID)->num_rows();
		$this->db->query("DELETE FROM `post_likes` WHERE `user_id`=" . $userID . " AND `post_id`=" . $postID);
		if ($liked == 1) {
			$this->db->query("INSERT INTO `post_likes` (`user_id`, `post_id`) VALUES (" . $userID . ", " . $postID . ")");
		}
	}
	
	public function delete_comment() {
		$id = intval($this->input->post('id'));
		$this->db->query("DELETE FROM `post_comments` WHERE `id`=" . $id);
	}
	
	public function update_comment() {
		$commentID = intval($this->input->post('comment_id'));
		$comment = $this->input->post('comment');
		$date = $this->input->post('date');
		$this->db->query("UPDATE `post_comments` SET `comment`='" . $comment . "', `date`='" . $date . "' WHERE `id`=" . $commentID);
	}
}
