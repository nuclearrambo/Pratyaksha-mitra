package com.pratyaksha;

public class Post {
	private String heading;
	private String postURL;
	private String description;
	private String imgURL;
	static String about = "‘प्रत्यक्ष-मित्र’ वेबसाईट यह मित्र के द्वारा मित्र से किया गया संवाद है और यह संवाद जैसे जैसे बढता रहेगा, वैसे वैसे यह मित्रता का रिश्ता अधिक से अधिक गहरा बनता जायेगा।\n" +
			"\n" +
			"‘दैनिक प्रत्यक्ष’ के कार्यकारी संपादक डॉ. अनिरुद्ध धैर्यधर जोशी की निरपेक्ष मित्रता का अनुभव गत कुछ वर्षों से प्राप्त करते समय, मित्रता का यह हाथ दुनिया भर के सारे इच्छुकों को प्राप्त होना चाहिए, इस भावना में से ‘प्रत्यक्ष-मित्र’ इस वेबसाईट की संकल्पना साकार हुई।\n" +
			"\n" +
			"इस वेबसाईट का ‘प्रत्यक्ष-मित्र’ यह नाम इसी मित्रता की भूमिका को स्पष्ट करता है। जो हितकारी है वही कहने वाला मित्र! वास्तव का ज्ञान कराने वाला मित्र! व्यक्तिगत, सामाजिक, राष्ट्रीय, वैश्विक इस तरह के हर एक धरातल पर सामान्य मानव के जीवन से जुडे विषयों का सुस्पष्ट, चुनिंदा शब्दों में प्रत्यक्ष बोध कराने वाला मित्र!\n" +
			"\n" +
			"इस वेबसाईट में क्या है? भारतीय संस्कृति के मूल्य, इतिहास, ज्ञान-विज्ञान, मनोरंजन, अध्यात्म, अर्थशास्त्र, पर्यटन इन जैसे अनेक विषयों से संबंधित जानकारी इस वेबसाईट पर उपलब्ध है और होती रहेगी।\n" +
			"\n" +
			"‘प्रत्यक्ष-मित्र’ इस वेबसाईट के माध्यम से, भारतभूमि से, यहाँ के पवित्र मूल्यों से प्रेम करने वाले हर एक के लिए मित्रता का एक अनोखा दालान खुल गया है।";

		public Post(String heading, String postURL, String description, String imgURL){
			super();
			this.heading = heading;
			this.postURL = postURL;
			this.description = description;
			this.imgURL = imgURL;
		}

		public String getHeading() {
			return heading;
		}

		public String getPostURL() {
			return postURL;
		}

		public String getDescription() {
			return description;
		}

		public String getImgURL() {
			return imgURL;
		}

		public void setHeading(String heading) {
			this.heading = heading;
		}

		public void setPostURL(String postURL) {
			this.postURL = postURL;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setImgURL(String imgURL) {
			this.imgURL = imgURL;
		}
		
		
}
