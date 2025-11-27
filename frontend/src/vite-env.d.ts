/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string;
  readonly VITE_CONTRACT_API_URL?: string;
  readonly VITE_WORKFLOW_API_URL?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

