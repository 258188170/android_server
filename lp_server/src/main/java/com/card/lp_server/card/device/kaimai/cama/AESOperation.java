package com.card.lp_server.card.device.kaimai.cama;


/**
 * 
 * 014提供的AES加解密算法，支持AES128 OFB/CFB/CBC/ECB
 * 
 * @author SEO-Dev
 *
 */
public class AESOperation {
	/**
	 * 加电计时器数据加密
	 * 
	 * @param src    明文
	 * @param offset 偏移
	 * @param dst    密文
	 * @return 是否成功
	 */
	public static boolean aes128ofbEncrypt(byte[] src, int offset, byte[] dst) {
		return CamaAes128ofb.encrypt(src, offset, dst);
	}

	/**
	 * 加电计时器数据解密
	 * 
	 * @param src    密文
	 * @param offset 偏移
	 * @param dst    明文
	 * @return 是否成功
	 */
	public static boolean aes128ofbDecrypt(byte[] src, int offset, byte[] dst) {
		return CamaAes128ofb.decrypt(src, offset, dst);
	}

	public static void main(String[] args) {
		/*
		byte[] src = new byte[] { (byte) -55, 24, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
		byte[] dst = new byte[16];
		System.out.print("src: ");
		print(src, 16);
		aes128ofbEncrypt(src, 0, dst);
		System.out.print("dst: ");
		print(dst, 16);
		aes128ofbDecrypt(dst, 0, src);
		System.out.print("src: ");
		print(src, 16);

		String[] devPaths = HidDevice.getHidDevices(0x0483, 0x5750);
		System.out.println(devPaths.length + " device" + (devPaths.length > 1 ? "s" : "") + " found");
		if (devPaths.length > 0) {
			HidDevice dev = new HidDevice(devPaths[0]);
			byte[] req = new byte[65];

			req[1] = (byte) 0xaa;
			req[2] = 0x55;
			req[3] = 0x10;
			req[4] = 0x10;
			req[15] = 0x55;
			req[16] = (byte) 0xaa;

			boolean ret = dev.setHidReportInterrupt(req);
			if (!ret) {
				System.out.println("发送请求失败");
				return;
			}

			byte[] res = new byte[65];
			ret = dev.getHidReportInterrupt(res);
			if (!ret) {
				System.out.println("接收响应失败");
				return;
			}

			if (!aes128ofbDecrypt(res, 5, dst)) {
				System.out.println("解密失败");
				return;
			}

			try {
				// 首字节固定为0x11
				// 获取本次通电时间、总通电时间和通电次数
				int[] nums = new int[] {
						((((((dst[1] & 0xff) << 8) | (dst[2] & 0xff)) << 8) | (dst[3] & 0xff)) << 8) | (dst[4] & 0xff),
						((((((dst[5] & 0xff) << 8) | (dst[6] & 0xff)) << 8) | (dst[7] & 0xff)) << 8) | (dst[8] & 0xff),
						((((((dst[9] & 0xff) << 8) | (dst[10] & 0xff)) << 8) | (dst[11] & 0xff)) << 8)
								| (dst[12] & 0xff) };
				// 获取弹号和导引头号
				aes128ofbDecrypt(res, 21, dst);
				String mn = new String(dst, "GBK");
				aes128ofbDecrypt(res, 37, dst);
				String tn = new String(dst, "GBK").trim();

				System.out.printf("弹    号: %s\n", mn);
				System.out.printf("导 引 头: %s\n", tn);
				System.out.printf("本次通电: %d秒\n", nums[0]);
				System.out.printf("总 通 电: %02d:%02d:%02d", nums[1] / 3600, (nums[1] % 3600) / 60, nums[1] % 60);
				System.out.printf("通电次数: %d", nums[2]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
		*/

	}

	/**
	 * 014提供的AES加解密密钥与初始向量
	 */
	protected static class CamaAes128ofb {

		private static final byte[] iv = { (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0a, (byte) 0x04, (byte) 0x05,
				(byte) 0x06, (byte) 0x00, (byte) 0x0b, (byte) 0x0c, (byte) 0x0d, (byte) 0x01, (byte) 0x02, (byte) 0x03,
				(byte) 0x0e, (byte) 0x0f };
		private static final byte[] key = { (byte) 0x24, (byte) 0x1f, (byte) 0x38, (byte) 0x16, (byte) 0x28,
				(byte) 0xbc, (byte) 0xd2, (byte) 0xae, (byte) 0xab, (byte) 0xf7, (byte) 0x5b, (byte) 0x64, (byte) 0x09,
				(byte) 0x3c, (byte) 0x4f, (byte) 0x3c };

		public static boolean encrypt(byte[] src, int offset, byte[] dst) {
			AESOperation moo = new AESOperation();
			moo.setKey(key);
			moo.setMode(MODE_OFB);
			moo.setIv(iv);
			byte[] s = src;
			if (offset != 0) {
				s = new byte[src.length - offset];
				System.arraycopy(src, offset, s, 0, s.length);
			}
			int len = moo.encrypt(s, 16, dst);

			return len == 16;
		}

		public static boolean decrypt(byte[] src, int offset, byte[] dst) {
			AESOperation moo = new AESOperation();
			moo.setKey(key);
			moo.setMode(MODE_OFB);
			moo.setIv(iv);
			byte[] s = src;
			if (offset != 0) {
				s = new byte[src.length - offset];
				System.arraycopy(src, offset, s, 0, s.length);
			}
			int len = moo.decrypt(s, 16, dst);

			return len == 16;
		}

	}

	/***************************** 以下由C语言直译，不要太关心 *******************************/
	public static final int MODE_OFB = 1;
	public static final int MODE_CFB = 2;
	public static final int MODE_CBC = 3;
	public static final int MODE_ECB = 4;

	private Aes aes;
	private int mode;
	private byte[] key = new byte[16];
	private byte[] iv = new byte[16];

	public AESOperation() {
		mode = MODE_ECB;
		aes = new Aes(key);
	}

	public void setAes(Aes aes) {
		this.aes = aes;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setKey(byte[] key) {
		if (key == null || key.length != 16)
			throw new RuntimeException("密钥不能为空且必须为16字节");
		System.arraycopy(key, 0, this.key, 0, 16);
		aes.setKey(key);
	}

	public void setIv(byte[] iv) {
		if (iv == null || iv.length != 16)
			throw new RuntimeException("初始向量不能为空且必须为16字节");
		System.arraycopy(iv, 0, this.iv, 0, 16);
	}

	public int encrypt(byte[] _in, int _length, byte[] _out) {
		boolean first_round = true;
		int rounds = 0;
		int start = 0;
		int end = 0;
		byte[] input = new byte[16];
		byte[] output = new byte[16];
		byte[] ciphertext = new byte[16];
		byte[] cipherout = new byte[256];
		byte[] plaintext = new byte[16];
		int co_index = 0;
		// 1. get rounds
		if (_length % 16 == 0) {
			rounds = _length / 16;
		} else {
			rounds = _length / 16 + 1;
		}
		// 2. for all rounds
		for (int j = 0; j < rounds; ++j) {
			start = j * 16;
			end = j * 16 + 16;
			if (end > _length)
				end = _length; // end of input
			// 3. copyt input to m_plaintext
			// memset(plaintext, 0, 16);
			System.arraycopy(new byte[16], 0, plaintext, 0, 16);
			// memcpy(plaintext, _in + start, end - start);
			System.arraycopy(_in, start, plaintext, 0, end - start);
			// 4. handle all modes
			if (mode == MODE_CFB) {
				if (first_round == true) {
					aes.cipher(iv, output);
					first_round = false;
				} else {
					aes.cipher(input, output);
				}
				for (int i = 0; i < 16; ++i) {
					if ((end - start) - 1 < i) {
						ciphertext[i] = (byte) ((0 ^ output[i]) & 0xff);
					} else {
						ciphertext[i] = (byte) (plaintext[i] ^ output[i]);
					}
				}
				for (int k = 0; k < end - start; ++k) {
					cipherout[co_index++] = ciphertext[k];
				}
				// memset(input,0, 16);
				// memcpy(input,ciphertext, 16);
				System.arraycopy(ciphertext, 0, input, 0, 16);
			} else if (mode == MODE_OFB) { // MODE_OFB
				if (first_round == true) {
					aes.cipher(iv, output); //
					first_round = false;
				} else {
					aes.cipher(input, output);
				}
				// ciphertext = plaintext ^ output
				for (int i = 0; i < 16; ++i) {
					if ((end - start) - 1 < i) {
						ciphertext[i] = (byte) ((0 ^ output[i]) & 0xff);
					} else {
						ciphertext[i] = (byte) (plaintext[i] ^ output[i]);
					}
				}
				//
				for (int k = 0; k < end - start; ++k) {
					cipherout[co_index++] = ciphertext[k];
				}
				// memset(input,0,16);
				// memcpy(input, output, 16);
				System.arraycopy(output, 0, input, 0, 16);
			} else if (mode == MODE_CBC) { // MODE_CBC
				System.out.println("-----plaintext------");
				print(plaintext, 16);
				System.out.println("--------------------");
				// printf("-----m_iv-----------\n");
				// print (m_iv, 16);
				// printf("--------------------\n");
				for (int i = 0; i < 16; ++i) {
					if (first_round == true) {
						input[i] = (byte) (plaintext[i] ^ iv[i]);
					} else {
						input[i] = (byte) (plaintext[i] ^ ciphertext[i]);
					}
				}
				first_round = false;
				// printf("^^^^^^^^^^^^\n");
				// print(input, 16);
				// printf("^^^^^^^^^^^^\n");
				aes.cipher(input, ciphertext);
				System.out.println("****ciphertext****");
				print(ciphertext, 16);
				System.out.println("************");
				for (int k = 0; k < end - start; ++k) {
					cipherout[co_index++] = ciphertext[k];
				}
				// memcpy(cipherout, ciphertext, 16);
				// co_index = 16;
			} else if (mode == MODE_ECB) {
				// TODO:
			}
		}
		// memcpy(_out, cipherout, co_index);
		System.arraycopy(cipherout, 0, _out, 0, co_index);
		return co_index;
	}

	public int decrypt(byte[] _in, int _length, byte[] _out) {
		boolean first_round = true;
		int rounds = 0;
		byte[] ciphertext = new byte[16];
		byte[] input = new byte[16];
		byte[] output = new byte[16];
		byte[] plaintext = new byte[16];
		byte[] plainout = new byte[256];
		int po_index = 0;
		if (_length % 16 == 0) {
			rounds = _length / 16;
		} else {
			rounds = _length / 16 + 1;
		}

		int start = 0;
		int end = 0;

		for (int j = 0; j < rounds; j++) {
			start = j * 16;
			end = start + 16;
			if (end > _length) {
				end = _length;
			}
			// memset(ciphertext, 0, 16);
			System.arraycopy(new byte[16], 0, ciphertext, 0, 16);
			// memcpy(ciphertext, _in + start, end - start);
			System.arraycopy(_in, start, ciphertext, 0, end - start);
			if (mode == MODE_CFB) {
				if (first_round == true) {
					aes.cipher(iv, output);
					first_round = false;
				} else {
					aes.cipher(input, output);
				}
				for (int i = 0; i < 16; i++) {
					if (end - start - 1 < i) {
						plaintext[i] = (byte) ((output[i] ^ 0) & 0xff);
					} else {
						plaintext[i] = (byte) (output[i] ^ ciphertext[i]);
					}
				}
				for (int k = 0; k < end - start; ++k) {
					plainout[po_index++] = plaintext[k];
				}
				// memset(input, 0, 16);
				// memcpy(input, ciphertext, 16);
				System.arraycopy(ciphertext, 0, input, 0, 16);
			} else if (mode == MODE_OFB) {
				if (first_round == true) {
					aes.cipher(iv, output);
					first_round = false;
				} else {
					aes.cipher(input, output);
				}
				for (int i = 0; i < 16; i++) {
					if (end - start - 1 < i) {
						plaintext[i] = (byte) ((0 ^ ciphertext[i]) & 0xff);
						first_round = false;
					} else {
						plaintext[i] = (byte) (output[i] ^ ciphertext[i]);
					}
				}
				for (int k = 0; k < end - start; ++k) {
					plainout[po_index++] = plaintext[k];
				}
				// memcpy(input, output, 16);
				System.arraycopy(output, 0, input, 0, 16);
			} else if (mode == MODE_CBC) {
				System.out.println("------ciphertext------");
				print(ciphertext, 16);
				System.out.print("----------------------\n");
				aes.invCipher(ciphertext, output);
				System.out.print("------output------");
				print(output, 16);
				System.out.print("----------------------\n");
				for (int i = 0; i < 16; ++i) {
					if (first_round == true) {
						plaintext[i] = (byte) (iv[i] ^ output[i]);
					} else {
						plaintext[i] = (byte) (input[i] ^ output[i]);
					}
				}
				first_round = false;
				for (int k = 0; k < end - start; ++k) {
					plainout[po_index++] = plaintext[k];
				}
				// memcpy(input, ciphertext, 16);
				System.arraycopy(ciphertext, 0, input, 0, 16);
			} else {
				// TODO
			}
		}
		// memcpy(_out, plainout, po_index);
		System.arraycopy(plainout, 0, _out, 0, po_index);
		return po_index;
	}

	private static void print(byte[] state, int len) {
		int i;
		for (i = 0; i < len; i++) {
			if (i % 16 == 0)
				System.out.println();
			System.out.printf("%02x ", state[i] & 0xff);
		}
		System.out.println();
	}

	protected static class Aes {
		public Aes(byte[] key) {
			if (key != null)
				keyExpansion(key, w);
		}

		public void setKey(byte[] key) {
			keyExpansion(key, w);
		}

		public byte[] cipher(byte[] input, byte[] output) {
			byte[][] state = new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] };
			int i, r, c;

			for (r = 0; r < 4; r++) {
				for (c = 0; c < 4; c++) {
					state[r][c] = input[c * 4 + r];
				}
			}

			addRoundKey(state, w[0]);

			for (i = 1; i <= 10; i++) {
				subBytes(state);
				shiftRows(state);
				if (i != 10)
					mixColumns(state);
				addRoundKey(state, w[i]);
			}

			for (r = 0; r < 4; r++) {
				for (c = 0; c < 4; c++) {
					output[c * 4 + r] = state[r][c];
				}
			}

			return output;
		}

		public byte[] invCipher(byte[] input, byte[] output) {
			byte[][] state = new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] };
			int i, r, c;

			for (r = 0; r < 4; r++) {
				for (c = 0; c < 4; c++) {
					state[r][c] = input[c * 4 + r];
				}
			}

			addRoundKey(state, w[10]);
			for (i = 9; i >= 0; i--) {
				invShiftRows(state);
				invSubBytes(state);
				addRoundKey(state, w[i]);
				if (i != 0) {
					invMixColumns(state);
				}
			}

			for (r = 0; r < 4; r++) {
				for (c = 0; c < 4; c++) {
					output[c * 4 + r] = state[r][c];
				}
			}
			return output;
		}

		public byte[] cipher(byte[] input, byte[] output, int length) {
			int i;
			if (0 == length) {
				while (input[length] != 0)
					length++;
			}
			for (i = 0; i < length; i += 16) {
				byte[] inp = new byte[16], outp = new byte[16];
				System.arraycopy(input, i, inp, 0, 16);
				cipher(inp, outp);
				System.arraycopy(outp, 0, output, i, 16);
			}
			return output;
		}

		public byte[] invCipher(byte[] input, byte[] output, int length) {
			int i;
			if (0 == length) {
				while (input[length] != 0)
					length++;
			}
			for (i = 0; i < length; i += 16) {
				byte[] inp = new byte[16], outp = new byte[16];
				System.arraycopy(input, i, inp, 0, 16);
				invCipher(inp, outp);
				System.arraycopy(outp, 0, output, i, 16);
			}
			return output;
		}

		private void keyExpansion(byte[] key, byte[][][] w) {
			int i, j, r, c;
			byte[] rc = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte) 0x80, 0x1b, 0x36 };
			for (r = 0; r < 4; r++) {
				for (c = 0; c < 4; c++) {
					w[0][r][c] = key[r + c * 4];
				}
			}
			for (i = 1; i <= 10; i++) {
				for (j = 0; j < 4; j++) {
					byte[] t = new byte[4];
					for (r = 0; r < 4; r++) {
						t[r] = j != 0 ? w[i][r][j - 1] : w[i - 1][r][3];
					}
					if (j == 0) {
						byte temp = t[0];
						for (r = 0; r < 3; r++) {
							t[r] = sBox[t[(r + 1) % 4] & 0xff];
						}
						t[3] = sBox[temp & 0xff];
						t[0] ^= rc[i - 1];
					}
					for (r = 0; r < 4; r++) {
						w[i][r][j] = (byte) (w[i - 1][r][j] ^ t[r]);
					}
				}
			}
		}

		private byte ffmul(byte a, byte b) {
			byte[] bw = new byte[4];
			byte res = 0;
			int i;
			bw[0] = b;
			for (i = 1; i < 4; i++) {
				bw[i] = (byte) ((bw[i - 1] << 1) & 0xff);
				if ((bw[i - 1] & 0x80) != 0) {
					bw[i] ^= 0x1b;
				}
			}
			for (i = 0; i < 4; i++) {
				if (((a >> i) & 0x01) != 0) {
					res ^= bw[i];
				}
			}
			return res;
		}

		private void subBytes(byte[][] state) {
			int r, c;
			for (r = 0; r < 4; r++) {
				for (c = 0; c < 4; c++) {
					state[r][c] = sBox[state[r][c] & 0xff];
				}
			}
		}

		private void shiftRows(byte[][] state) {
			byte[] t = new byte[4];
			int r, c;
			for (r = 1; r < 4; r++) {
				for (c = 0; c < 4; c++) {
					t[c] = state[r][(c + r) % 4];
				}
				for (c = 0; c < 4; c++) {
					state[r][c] = t[c];
				}
			}
		}

		private void mixColumns(byte[][] state) {
			byte[] t = new byte[4];
			int r, c;
			for (c = 0; c < 4; c++) {
				for (r = 0; r < 4; r++) {
					t[r] = state[r][c];
				}
				for (r = 0; r < 4; r++) {
					state[r][c] = (byte) ((ffmul((byte) 0x02, t[r]) ^ ffmul((byte) 0x03, t[(r + 1) % 4])
							^ ffmul((byte) 0x01, t[(r + 2) % 4]) ^ ffmul((byte) 0x01, t[(r + 3) % 4])) & 0xff);
				}
			}
		}

		private void addRoundKey(byte[][] state, byte[][] k) {
			int r, c;
			for (c = 0; c < 4; c++) {
				for (r = 0; r < 4; r++) {
					state[r][c] ^= k[r][c];
				}
			}
		}

		private void invSubBytes(byte[][] state) {
			int r, c;
			for (r = 0; r < 4; r++) {
				for (c = 0; c < 4; c++) {
					state[r][c] = invSbox[state[r][c] & 0xff];
				}
			}
		}

		private void invShiftRows(byte[][] state) {
			byte[] t = new byte[4];
			int r, c;
			for (r = 1; r < 4; r++) {
				for (c = 0; c < 4; c++) {
					t[c] = state[r][(c - r + 4) % 4];
				}
				for (c = 0; c < 4; c++) {
					state[r][c] = t[c];
				}
			}
		}

		private void invMixColumns(byte[][] state) {
			byte[] t = new byte[4];
			int r, c;
			for (c = 0; c < 4; c++) {
				for (r = 0; r < 4; r++) {
					t[r] = state[r][c];
				}
				for (r = 0; r < 4; r++) {
					state[r][c] = (byte) ((ffmul((byte) 0x0e, t[r]) ^ ffmul((byte) 0x0b, t[(r + 1) % 4])
							^ ffmul((byte) 0x0d, t[(r + 2) % 4]) ^ ffmul((byte) 0x09, t[(r + 3) % 4])) & 0xff);
				}
			}
		}

		private byte[] sBox = { /* 0 1 2 3 4 5 6 7 8 9 a b c d e f */
				(byte) 0x63, (byte) 0x7c, (byte) 0x77, (byte) 0x7b, (byte) 0xf2, (byte) 0x6b, (byte) 0x6f, (byte) 0xc5,
				(byte) 0x30, (byte) 0x01, (byte) 0x67, (byte) 0x2b, (byte) 0xfe, (byte) 0xd7, (byte) 0xab,
				(byte) 0x76, /* 0 */
				(byte) 0xca, (byte) 0x82, (byte) 0xc9, (byte) 0x7d, (byte) 0xfa, (byte) 0x59, (byte) 0x47, (byte) 0xf0,
				(byte) 0xad, (byte) 0xd4, (byte) 0xa2, (byte) 0xaf, (byte) 0x9c, (byte) 0xa4, (byte) 0x72,
				(byte) 0xc0, /* 1 */
				(byte) 0xb7, (byte) 0xfd, (byte) 0x93, (byte) 0x26, (byte) 0x36, (byte) 0x3f, (byte) 0xf7, (byte) 0xcc,
				(byte) 0x34, (byte) 0xa5, (byte) 0xe5, (byte) 0xf1, (byte) 0x71, (byte) 0xd8, (byte) 0x31,
				(byte) 0x15, /* 2 */
				(byte) 0x04, (byte) 0xc7, (byte) 0x23, (byte) 0xc3, (byte) 0x18, (byte) 0x96, (byte) 0x05, (byte) 0x9a,
				(byte) 0x07, (byte) 0x12, (byte) 0x80, (byte) 0xe2, (byte) 0xeb, (byte) 0x27, (byte) 0xb2,
				(byte) 0x75, /* 3 */
				(byte) 0x09, (byte) 0x83, (byte) 0x2c, (byte) 0x1a, (byte) 0x1b, (byte) 0x6e, (byte) 0x5a, (byte) 0xa0,
				(byte) 0x52, (byte) 0x3b, (byte) 0xd6, (byte) 0xb3, (byte) 0x29, (byte) 0xe3, (byte) 0x2f,
				(byte) 0x84, /* 4 */
				(byte) 0x53, (byte) 0xd1, (byte) 0x00, (byte) 0xed, (byte) 0x20, (byte) 0xfc, (byte) 0xb1, (byte) 0x5b,
				(byte) 0x6a, (byte) 0xcb, (byte) 0xbe, (byte) 0x39, (byte) 0x4a, (byte) 0x4c, (byte) 0x58,
				(byte) 0xcf, /* 5 */
				(byte) 0xd0, (byte) 0xef, (byte) 0xaa, (byte) 0xfb, (byte) 0x43, (byte) 0x4d, (byte) 0x33, (byte) 0x85,
				(byte) 0x45, (byte) 0xf9, (byte) 0x02, (byte) 0x7f, (byte) 0x50, (byte) 0x3c, (byte) 0x9f,
				(byte) 0xa8, /* 6 */
				(byte) 0x51, (byte) 0xa3, (byte) 0x40, (byte) 0x8f, (byte) 0x92, (byte) 0x9d, (byte) 0x38, (byte) 0xf5,
				(byte) 0xbc, (byte) 0xb6, (byte) 0xda, (byte) 0x21, (byte) 0x10, (byte) 0xff, (byte) 0xf3,
				(byte) 0xd2, /* 7 */
				(byte) 0xcd, (byte) 0x0c, (byte) 0x13, (byte) 0xec, (byte) 0x5f, (byte) 0x97, (byte) 0x44, (byte) 0x17,
				(byte) 0xc4, (byte) 0xa7, (byte) 0x7e, (byte) 0x3d, (byte) 0x64, (byte) 0x5d, (byte) 0x19,
				(byte) 0x73, /* 8 */
				(byte) 0x60, (byte) 0x81, (byte) 0x4f, (byte) 0xdc, (byte) 0x22, (byte) 0x2a, (byte) 0x90, (byte) 0x88,
				(byte) 0x46, (byte) 0xee, (byte) 0xb8, (byte) 0x14, (byte) 0xde, (byte) 0x5e, (byte) 0x0b,
				(byte) 0xdb, /* 9 */
				(byte) 0xe0, (byte) 0x32, (byte) 0x3a, (byte) 0x0a, (byte) 0x49, (byte) 0x06, (byte) 0x24, (byte) 0x5c,
				(byte) 0xc2, (byte) 0xd3, (byte) 0xac, (byte) 0x62, (byte) 0x91, (byte) 0x95, (byte) 0xe4,
				(byte) 0x79, /* a */
				(byte) 0xe7, (byte) 0xc8, (byte) 0x37, (byte) 0x6d, (byte) 0x8d, (byte) 0xd5, (byte) 0x4e, (byte) 0xa9,
				(byte) 0x6c, (byte) 0x56, (byte) 0xf4, (byte) 0xea, (byte) 0x65, (byte) 0x7a, (byte) 0xae,
				(byte) 0x08, /* b */
				(byte) 0xba, (byte) 0x78, (byte) 0x25, (byte) 0x2e, (byte) 0x1c, (byte) 0xa6, (byte) 0xb4, (byte) 0xc6,
				(byte) 0xe8, (byte) 0xdd, (byte) 0x74, (byte) 0x1f, (byte) 0x4b, (byte) 0xbd, (byte) 0x8b,
				(byte) 0x8a, /* c */
				(byte) 0x70, (byte) 0x3e, (byte) 0xb5, (byte) 0x66, (byte) 0x48, (byte) 0x03, (byte) 0xf6, (byte) 0x0e,
				(byte) 0x61, (byte) 0x35, (byte) 0x57, (byte) 0xb9, (byte) 0x86, (byte) 0xc1, (byte) 0x1d,
				(byte) 0x9e, /* d */
				(byte) 0xe1, (byte) 0xf8, (byte) 0x98, (byte) 0x11, (byte) 0x69, (byte) 0xd9, (byte) 0x8e, (byte) 0x94,
				(byte) 0x9b, (byte) 0x1e, (byte) 0x87, (byte) 0xe9, (byte) 0xce, (byte) 0x55, (byte) 0x28,
				(byte) 0xdf, /* e */
				(byte) 0x8c, (byte) 0xa1, (byte) 0x89, (byte) 0x0d, (byte) 0xbf, (byte) 0xe6, (byte) 0x42, (byte) 0x68,
				(byte) 0x41, (byte) 0x99, (byte) 0x2d, (byte) 0x0f, (byte) 0xb0, (byte) 0x54, (byte) 0xbb,
				(byte) 0x16 /* f */
		};
		private byte[] invSbox = { /* 0 1 2 3 4 5 6 7 8 9 a b c d e f */
				(byte) 0x52, (byte) 0x09, (byte) 0x6a, (byte) 0xd5, (byte) 0x30, (byte) 0x36, (byte) 0xa5, (byte) 0x38,
				(byte) 0xbf, (byte) 0x40, (byte) 0xa3, (byte) 0x9e, (byte) 0x81, (byte) 0xf3, (byte) 0xd7,
				(byte) 0xfb, /* 0 */
				(byte) 0x7c, (byte) 0xe3, (byte) 0x39, (byte) 0x82, (byte) 0x9b, (byte) 0x2f, (byte) 0xff, (byte) 0x87,
				(byte) 0x34, (byte) 0x8e, (byte) 0x43, (byte) 0x44, (byte) 0xc4, (byte) 0xde, (byte) 0xe9,
				(byte) 0xcb, /* 1 */
				(byte) 0x54, (byte) 0x7b, (byte) 0x94, (byte) 0x32, (byte) 0xa6, (byte) 0xc2, (byte) 0x23, (byte) 0x3d,
				(byte) 0xee, (byte) 0x4c, (byte) 0x95, (byte) 0x0b, (byte) 0x42, (byte) 0xfa, (byte) 0xc3,
				(byte) 0x4e, /* 2 */
				(byte) 0x08, (byte) 0x2e, (byte) 0xa1, (byte) 0x66, (byte) 0x28, (byte) 0xd9, (byte) 0x24, (byte) 0xb2,
				(byte) 0x76, (byte) 0x5b, (byte) 0xa2, (byte) 0x49, (byte) 0x6d, (byte) 0x8b, (byte) 0xd1,
				(byte) 0x25, /* 3 */
				(byte) 0x72, (byte) 0xf8, (byte) 0xf6, (byte) 0x64, (byte) 0x86, (byte) 0x68, (byte) 0x98, (byte) 0x16,
				(byte) 0xd4, (byte) 0xa4, (byte) 0x5c, (byte) 0xcc, (byte) 0x5d, (byte) 0x65, (byte) 0xb6,
				(byte) 0x92, /* 4 */
				(byte) 0x6c, (byte) 0x70, (byte) 0x48, (byte) 0x50, (byte) 0xfd, (byte) 0xed, (byte) 0xb9, (byte) 0xda,
				(byte) 0x5e, (byte) 0x15, (byte) 0x46, (byte) 0x57, (byte) 0xa7, (byte) 0x8d, (byte) 0x9d,
				(byte) 0x84, /* 5 */
				(byte) 0x90, (byte) 0xd8, (byte) 0xab, (byte) 0x00, (byte) 0x8c, (byte) 0xbc, (byte) 0xd3, (byte) 0x0a,
				(byte) 0xf7, (byte) 0xe4, (byte) 0x58, (byte) 0x05, (byte) 0xb8, (byte) 0xb3, (byte) 0x45,
				(byte) 0x06, /* 6 */
				(byte) 0xd0, (byte) 0x2c, (byte) 0x1e, (byte) 0x8f, (byte) 0xca, (byte) 0x3f, (byte) 0x0f, (byte) 0x02,
				(byte) 0xc1, (byte) 0xaf, (byte) 0xbd, (byte) 0x03, (byte) 0x01, (byte) 0x13, (byte) 0x8a,
				(byte) 0x6b, /* 7 */
				(byte) 0x3a, (byte) 0x91, (byte) 0x11, (byte) 0x41, (byte) 0x4f, (byte) 0x67, (byte) 0xdc, (byte) 0xea,
				(byte) 0x97, (byte) 0xf2, (byte) 0xcf, (byte) 0xce, (byte) 0xf0, (byte) 0xb4, (byte) 0xe6,
				(byte) 0x73, /* 8 */
				(byte) 0x96, (byte) 0xac, (byte) 0x74, (byte) 0x22, (byte) 0xe7, (byte) 0xad, (byte) 0x35, (byte) 0x85,
				(byte) 0xe2, (byte) 0xf9, (byte) 0x37, (byte) 0xe8, (byte) 0x1c, (byte) 0x75, (byte) 0xdf,
				(byte) 0x6e, /* 9 */
				(byte) 0x47, (byte) 0xf1, (byte) 0x1a, (byte) 0x71, (byte) 0x1d, (byte) 0x29, (byte) 0xc5, (byte) 0x89,
				(byte) 0x6f, (byte) 0xb7, (byte) 0x62, (byte) 0x0e, (byte) 0xaa, (byte) 0x18, (byte) 0xbe,
				(byte) 0x1b, /* a */
				(byte) 0xfc, (byte) 0x56, (byte) 0x3e, (byte) 0x4b, (byte) 0xc6, (byte) 0xd2, (byte) 0x79, (byte) 0x20,
				(byte) 0x9a, (byte) 0xdb, (byte) 0xc0, (byte) 0xfe, (byte) 0x78, (byte) 0xcd, (byte) 0x5a,
				(byte) 0xf4, /* b */
				(byte) 0x1f, (byte) 0xdd, (byte) 0xa8, (byte) 0x33, (byte) 0x88, (byte) 0x07, (byte) 0xc7, (byte) 0x31,
				(byte) 0xb1, (byte) 0x12, (byte) 0x10, (byte) 0x59, (byte) 0x27, (byte) 0x80, (byte) 0xec,
				(byte) 0x5f, /* c */
				(byte) 0x60, (byte) 0x51, (byte) 0x7f, (byte) 0xa9, (byte) 0x19, (byte) 0xb5, (byte) 0x4a, (byte) 0x0d,
				(byte) 0x2d, (byte) 0xe5, (byte) 0x7a, (byte) 0x9f, (byte) 0x93, (byte) 0xc9, (byte) 0x9c,
				(byte) 0xef, /* d */
				(byte) 0xa0, (byte) 0xe0, (byte) 0x3b, (byte) 0x4d, (byte) 0xae, (byte) 0x2a, (byte) 0xf5, (byte) 0xb0,
				(byte) 0xc8, (byte) 0xeb, (byte) 0xbb, (byte) 0x3c, (byte) 0x83, (byte) 0x53, (byte) 0x99,
				(byte) 0x61, /* e */
				(byte) 0x17, (byte) 0x2b, (byte) 0x04, (byte) 0x7e, (byte) 0xba, (byte) 0x77, (byte) 0xd6, (byte) 0x26,
				(byte) 0xe1, (byte) 0x69, (byte) 0x14, (byte) 0x63, (byte) 0x55, (byte) 0x21, (byte) 0x0c,
				(byte) 0x7d /* f */
		};
		private byte[][][] w = new byte[][][] { new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] },
				new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] },
				new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] },
				new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] },
				new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] },
				new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] },
				new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] },
				new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] },
				new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] },
				new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] },
				new byte[][] { new byte[4], new byte[4], new byte[4], new byte[4] } };
	}
}
