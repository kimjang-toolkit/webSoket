import { reissueAccessTokenRequest } from '@/apis/authentication';
import store from '@/redux/store';
import { setToken } from '@/redux/userSlice';
import axios from 'axios';
import Cookies from 'js-cookie';

const api = axios.create({
  baseURL: `${import.meta.env.VITE_SPRING_URL}`,
});

export default api;

//요청인터셉터
api.interceptors.request.use(
  (config) => {
    const state = store.getState();
    const { accessToken } = state.user;

    if (accessToken) {
      config.headers['Authorization'] = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);
// 응답 인터셉터 추가
api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    // 요청이 실패했을 때 실행될 로직
    const originalRequest = error.config;
    if (error.response) {
      // 요청이 전송되었고 서버가 2xx 외의 상태 코드로 응답한 경우
      switch (error.response.status) {
        case 401:
          if (!originalRequest._retry) {
            originalRequest._retry = true;
          }
          try {
            const token = Cookies.get('refreshToken');
            if (!token) throw new Error('no refreshToken!');
            const { accessToken, refreshToken } = await reissueAccessTokenRequest(token);
            store.dispatch(setToken(accessToken));
            Cookies.set('refreshToken', refreshToken);
            axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
            originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;

            return api(originalRequest);
          } catch (refreshError) {
            console.error('Token refresh failed:', refreshError);
            Cookies.set('refreshToken', 'EXPIRED'); // 리프레시 토큰 삭제

            return Promise.reject(refreshError);
          }

          break;
        case 404:
          // 요청한 리소스를 찾을 수 없음
          console.error('Requested resource not found');
          break;
        // 기타 상태 코드 처리
        default:
          console.error(`Error: ${error.response.status} - ${error.response.statusText}`);
      }
    } else if (error.request) {
      // 요청이 전송되었지만 응답을 받지 못한 경우
      console.error('No response received:', error.request);
    } else {
      // 요청 설정 중에 발생한 에러
      console.error('Error setting up request:', error.message);
    }
    //오류 처리 후 throw하여 호출한 곳에서 처리할 수 있게 함.
    return Promise.reject(error);
  },
);
